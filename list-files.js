const fs = require('fs')
const fsp = require('fs/promises')


function listFiles(dir) {
  var names = fs.readdirSync(dir)
  var result = []
  for (let name of names) {
    var stat = fs.statSync(dir + name)
    if (stat.isFile()) {
      result.push(dir + name)
    } else if (stat.isDirectory()) {
      result.push(...listFiles(dir + name + '/'))
    }
  }
  return result
}

async function listFilesAsync(dir) {
  var names = await fsp.readdir(dir)
  var result = []
  for (let name of names) {
    var stat = await fsp.stat(dir + name)
    if (stat.isFile()) {
      result.push(dir + name)
    } else if (stat.isDirectory()) {
      result.push(...await listFilesAsync(dir + name + '/'))
    }
  }
  return result
}

function listFilesCB(dir, callback) {
  var result = []

  fs.readdir(dir, (err, names) => {
    let count = 0

    if (names.length == 0) {
      callback(result)
    } else {
      for (let name of names) {
        fs.stat(dir + name, (err, stat) => {
          if (stat.isFile()) {
            result.push(dir + name)
            count++ // 记录异步操作完成的数量
            if (count == names.length) {
              callback(result)
            }
          } else if (stat.isDirectory()) {
            listFilesCB(dir + name + '/', (files) => {
              result.push(...files)
              count++ // 记录异步操作完成的数量
              if (count == names.length) {
                callback(result)
              }
            })
          }
          
        })
      }
    }

  })
}

function listFilesP(dir) {
  var names
  return fsp.readdir(dir).then(result => {
    names = result
    return Promise.all(names.map(name => fsp.stat(dir + name)))
  }).then(stats => {
    return Promise.all(stats.map((stat, i) => {
      if (stat.isFile()) {
        return dir + names[i]
      } else if (stat.isDirectory()) {
        return listFilesP(dir + names[i] + '/')
      }
    }))
  }).then(mixedResult => {
    return mixedResult.flat()
  })
}

console.time('sync')
listFiles('./') 
console.timeEnd('sync')

// console.time('async')
// listFilesAsync('./').then(r => {
//   // console.log(r)
//   console.timeEnd('async')
// })

// console.time('p')
// listFilesP('./').then(r => {
//   // console.log(r)
//   console.timeEnd('p')
// })

// console.time('cb')
// listFilesCB('./', result => {
//   // console.log(result)
//   console.timeEnd('cb')
// })