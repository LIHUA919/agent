import fs from 'node:fs'

var bf = await fs.promises.readFile('./hello.js', 'utf8')

console.log( bf )