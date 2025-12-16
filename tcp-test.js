
// tcp模块，不过名字叫net
var net = require('net')

// 创建tcp服务器对象
var server = net.createServer()

// 让服务器对象在某端口侦听,成功侦听时触发函数
server.listen(33555,() => {
  console.log('listening on port', 33555)
})

// 每当服务器接收到一个连接时触发事件
server.on('connection', (conn) => {

  // 可以在连接对象上读取到远程ip和远程端口
  console.log(conn.remoteAddress, conn.remotePort)

  // 向连接的对方发送数据
  conn.write('hi!')

  // 当在连接上收到数据时触发
  conn.on('data', data => { // data的类型类似于Uint8Array，不是字符串，但可以toString直接转成字符串
    console.log( conn.remoteAddress, 'says', data.toString().slice(0,5) )

    conn.write(   data.toString().toUpperCase()   )
  })

  // 连接断开时触发，连接断开就像电话挂断，之后就不能再收发消息了
  conn.on('end', () => {
  })
})





// 做为客户端，连接tcp服务器：
// 创建向某ip/域名的某端口的tcp连接

conn = net.connect(33555, 'damiaoedu.com')
conn.write('data') // 向连接发送数据
// 返回true并不表示数据到达对方了，它有其它含义
// 返回true可以理解为“能继续发”
// 返回false一般表示连接已经断开了

conn.read(size) // 读取接收到的数据的若干字节
// 返回null时表示接收到的数据已读完了
