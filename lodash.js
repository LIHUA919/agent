
var lodash = function(){

  function sort(ary) {

  }
  function sortBy(ary, predicate) {

  }
  sortBy([{}, {}, {}], it => it.age)

  function sortWith(ary, compareFn) {
  }

  function clone(obj) {
    var copy = {}
    for (var key in obj) {
      var val = obj[key]
      copy[key] = val
    }
    return copy
  }

  function cloneDeep(obj, map = new Map()) {
    if (typeof obj !== 'object') {
      return obj
    }
    if (map.has(obj)) {
      return map.get(obj)
    }

    var copy = {}
    map.set(obj, copy)

    for (var key in obj) {
      var val = obj[key]
      if (val && typeof val === 'object') {
        copy[key] = cloneDeep(val, map)
      } else {
        copy[key] = val
      }
    }
    return copy
  }

  var users = [
    { 
      'user': {
        name: 'barney'
      }
    },
    { 
      'user': {
        name: 'fred'
      }
    },
  ];
  // The `_.property` iteratee shorthand.
  _.map(users, 'user'  );
  _.map(users, _.property('user')  );
  _.map(users, _.property('user.name')  );
  _.map(users, it => it.user     );
  // => ['barney', 'fred']

  function property(prop) {
    return function(obj) {
      return get(obj, prop)
    }
  }

  function property(prop) {
    return get.bind(null, prop)
  }

  function toPath(str) {
    return path.split('.')
  }

  getAdotB = get.bind(null, 'a.b')
  getAdotB(obj)

  function get(path, obj) {
    var paths = toPath(path)
    for (let path of paths) {
      if (obj == null) {
        break
      }
      obj = obj[path]
    }
    return obj
  }

  function matchesProperty([key, val]) {
    return function(obj) {
      return obj[key] === val
    }
  }


  // 判断source是否则obj的部分组成并且对应属性的值相同
  // 并且可以处理深层次对象
  function isMatch(source, obj) {

  }

  function matches(target) {
    return isMatch.bind(null, target)
  }

  function matches(target) {
    return function(obj) {
      for (var key in target) {
        if (typeof target[key] == 'object') {
          if (isMatch(target[key], obj[key]) == false) {
            return false
          }
        } else {
          if (obj[key] !== target[key]) {
            return false
          }
        }
      }
      return true
    }
  }

  //             f,    null,    _, _, 2
  function bind(func, thisArg, ...fixedArgs) {
    return function(...args) { // 0,0 , 1 ,5
      var j = 0
      var realArgs = fixedArgs.slice()

      for (var i = 0; i < realArgs.length; i++) {
        if (realArgs[i] === undefined) {
          realArgs[i] = args[j++]
        }
      }
      while(j < args.length) {
        realArgs.push(args[j++])
      }

      return func.apply(thisArg, realArgs)
    }
  }

  function iteratee(predicate) {
    if (typeof predicate == 'string') {
      predicate = property(predicate)
    } else if (Array.isArray(predicate)) {
      predicate = matchesProperty(predicate)
    } else if (typeof predicate == 'object') {
      predicate = matches(predicate)
    }
    return predicate
  }

  function filter(ary, predicate) {
    predicate = iteratee(predicate)

    var result = []
    for (var i = 0; i < ary.length; i++) {
      if (predicate(ary[i])) {
        result.push(ary[i])
      }
    }
    return result
  }

  function map(ary, mapper) {
    predicate = iteratee(predicate)

  }

  function identity(val) {
    return val
  }

  function sum(ary) {
    return sumBy(ary, identity)
  }
  function sumBy(ary, predicate) {}
  sumBy(peoples,  it => it.money)

  function sort(ary) {
    return sortBy(ary, identity)
  }

  function sortBy(ary, f) {

  }

  function curry(f, length = f.length) {
    return function(...args) {
      if (args.length < length) {
        return curry(f.bind(null, ...args), length - args.length)
      } else {
        return f(...args)
      }
    }
  }

  
}()