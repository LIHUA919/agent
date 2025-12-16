
const fs = require('node:fs')


process.stderr.pipe(fs.createWriteStream('./p-err.log'))
process.stdout.pipe(fs.createWriteStream('./p-out.log'))
