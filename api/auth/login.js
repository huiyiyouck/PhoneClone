const crypto = require('crypto')
const { query } = require('../_utils/db')
const { issueToken } = require('../_utils/jwt')

module.exports = async (req, res) => {
  if (req.method !== 'POST') {
    res.status(405).end()
    return
  }
  try {
    const { email, password } = req.body || {}
    if (!email || !password) {
      res.status(400).end()
      return
    }
    const hash = crypto.createHash('sha256').update(password, 'utf8').digest('hex')
    const out = await query(
      'SELECT id, username, email, membership_level FROM users WHERE email = $1 AND password_hash = $2',
      [email, hash]
    )
    if (out.rowCount === 0) {
      res.status(401).end()
      return
    }
    const u = out.rows[0]
    const { token, expiresIn } = issueToken(u.email)
    await query(
      'INSERT INTO user_sessions (user_id, token, expires_at, created_at) VALUES ($1,$2, CURRENT_TIMESTAMP + INTERVAL \'' + Math.floor(expiresIn / 1000) + ' seconds\', CURRENT_TIMESTAMP) ON CONFLICT (token) DO NOTHING',
      [u.id, token]
    )
    res.json({ token, username: u.username, email: u.email, membershipLevel: u.membership_level, expiresIn })
  } catch (e) {
    res.status(500).end()
  }
}