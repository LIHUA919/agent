const fs = require('node:fs')
const fsp = fs.promises
const http = require('node:http')
const path = require('node:path')
const mime = require('mime-types')


const PORT = 8081

const baseDir = path.resolve(process.argv[2] ?? './')

const server = http.createServer()


/**
 * 
 * 文件夹路径末尾没有斜杠，需要跳转到带斜杠的地址
 * url中带有querystring，qs是不能作为文件名的，需要去掉
 * 如果url对应文件夹，这应该列出文件夹内容
 *   列出内容时文件夹在前，文件在后
 * 列出内容时，应该区分文件与文件夹，最好能显示文件大小
 * 除根目录以外，每个目录都要显示一个回到上级目录的链接
 * 文件夹中如果有index.html文件，则返回这个文件，而不是列出文件夹内容
 *   但如果index.html不是文件而是文件夹或其它奇怪的文件，则照常显示文件夹的内容
 * 文件名中有中文时要正确处理
 * 为每个文件的响应给出合适的content-type响应头以表示文件的媒体类型
 * 确保只向客户端返回baseDir文件夹内的内容，而不存在返回系统中其它文件的可能性
 *   （这是一个重要的安全问题，如果处理不好，有可能泄露系统中的重要文件）
 * 文件的大小显示为人类可读模式
 */

server.on('request', (req, res) => {
  var urlObj = new URL(`http://host${req.url}`)

  var targetPath = path.join(baseDir, decodeURIComponent(urlObj.pathname))

  if (!targetPath.startsWith(baseDir)) {
    res.writeHead(400, {
      'content-type': 'text/html; charset=utf8'
    })
    res.end('坏银，休想偷我yan照！')
    return
  }

  console.log(req.method, decodeURIComponent(req.url), targetPath)

  try {
    var stat = fs.statSync(targetPath)

    if (stat.isFile()) {
      var data = fs.readFileSync(targetPath)
      res.writeHead(200, {
        'content-type': mime.contentType(targetPath) // 这里可能 是有问题的，传入./aaa.txt时会输出不对
      })
      res.write(data)
      res.end()
    } else if (stat.isDirectory()) {
      if (!urlObj.pathname.endsWith('/')) {
        res.writeHead(302, {
          location: urlObj.pathname + '/' + urlObj.search
        })
        res.end()
        return
      }

      const indexPath = path.join(targetPath, 'index.html')

      try {
        const stat = fs.statSync(indexPath)

        if (stat.isFile()) {
          // 返回文件内容
          const content = fs.readFileSync(indexPath)
          res.end(content)
        } else {
          throw new Error('index.html is not a file')
        }
      } catch(e) {

        // 列出文件夹的内容
        var entries = fs.readdirSync(targetPath, {withFileTypes: true})

        entries.forEach(entry => {
          var stat = fs.statSync(path.join(targetPath, entry.name))
          if (stat.isFile()) {
            entry.size = stat.size
          } else {
            entry.size = 0
          }
        })
  
        var page = `
          <h1>Index of ${urlObj.pathname}</h1>
          <table>
          ${// 除根目录以外，每个目录都要显示一个回到上级目录的链接
            urlObj.pathname == '/'
            ? ''
            : '<tr><td></td><td><a href="../">../</a></td></tr>'
          }
          ${
            entries.map((entry) => {
              var sep = entry.isFile() ? '' : '/'

              return `<tr><td>${entry.isFile() ? entry.size : ''}</td><td><a href="${entry.name}${sep}">${entry.name}${sep}</a></td></tr>`
            }).join('')
          }
          </table>
          <p>
            <address>Node.js ${process.version}/ http-server server running @ 192.168.3.152:${PORT}</address>
          </p>
        `
        res.writeHead(200, {
          'content-type': 'text/html; charset=utf8'
        })
        res.write(page)
        res.end()
      }
    }
  } catch(e) {
    console.log(e)
    if (e.code == 'ENOENT') {
      res.writeHead(404)
      res.write('File Not Found')
      res.end()
    } else {  // robust  鲁棒性  健壮性   句柄   套接字
      res.writeHead(500)
      res.write(e.message)
      res.end()
    }
  }
})

// server.emit('request', a, b)



server.listen(PORT, () => {
  console.log('server listening on port', PORT)
  console.log('serving', baseDir)
})


