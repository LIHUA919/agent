// worker监听外部发来的消息
this.addEventListener("message", function ( event ) {
  var result = event.data * event.data
  postMessage(result);
});


// for (var i = 0; i < 30; i++) {
//   console.log(i, 'workder')
//   sleep(1000)
// }

// 死循环多长时间
function sleep(time) {
  var start = Date.now()
  while(Date.now() - start < time) {

  }
}