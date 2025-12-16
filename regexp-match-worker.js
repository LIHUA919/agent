

this.addEventListener('message', e => {
  
  var data = e.data
  var re = data.re
  var string = data.string

  var matches = [] // 记录所有的匹配
  var match = null

  while (match = re.exec(string)) {
    matches.push(match)
    if (re.global == false) { // 如果没有g标志，这个循环一定是个死循环
      break
    }
  }

  postMessage(matches)
})