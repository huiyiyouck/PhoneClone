const jwt = require('jsonwebtoken')

function issueToken(email, expiresInMs = 86400000) {
  const secret = process.env.JWT_SECRET
  const token = jwt.sign({ sub: email }, secret, { expiresIn: Math.floor(expiresInMs / 1000) })
  return { token, expiresIn: expiresInMs }
}

module.exports = { issueToken }