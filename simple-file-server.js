var http = require("node:http");
var fs = require("node:fs");
var urlMod = require("node:url");
const mime = require("mime-types");
const path = require("node:path");

var port = 8001;

var methods = Object.create(null);

// methods.GET = function (targetPath, respond) {
//   fs.stat(targetPath, function (error, stats) {
//     if (error && error.code == "ENOENT") respond(404, "File not found");
//     else if (error) {
//       console.log(error);
//       respond(500, error.toString());
//     } else if (stats.isDirectory())
//       fs.readdir(targetPath, function (error, files) {
//         if (error) respond(500, error.toString());
//         else respond(200, files.join("\n"));
//       });
//     else respond(200, fs.createReadStream(targetPath), mime.contentType(path.resolve(targetPath)));
//   });
// };
methods.GET = function (targetPath, respond) {
  fs.stat(targetPath, function (error, stats) {
    if (error && error.code == "ENOENT") respond(404, "File not found");
    else if (error) {
      console.log(error);
      respond(500, error.toString());
    } else if (stats.isDirectory())
      fs.readdir(targetPath,{withFileTypes: true}, function (error, entries) {
        if (error) {
          respond(500, error.toString())
        } else {
          
          respond(200, JSON.stringify(entries.map(entry => {
            return {
              name: entry.name,
              isDir: entry.isDirectory(),
              size: fs.statSync(path.join(targetPath, entry.name)).size,
            }
          })));
        }
      });
    else respond(200, fs.createReadStream(targetPath), mime.contentType(path.resolve(targetPath)));
  });
};

// methods.GETsync = function (targetPath, respond) {
//   try {
//     var stat = fs.statSync(targetPath)
//     if (stat.isDirectory() ) {
//       var names = fs.readdirSync(targetPath)
//       respondErrorOrNothing
//     }
//   } catch(e) {
//     if (e.code == 'ENOENT') {
//       respond(404, "File not found");
//     } else {
//       throw e
//     }
//   }
//   fs.stat(targetPath, function (error, stats) {
//     if (error && error.code == "ENOENT") respond(404, "File not found");
//     else if (error) {
//       console.log(error);
//       respond(500, error.toString());
//     } else if (stats.isDirectory())
//       fs.readdir(targetPath, function (error, files) {
//         if (error) respond(500, error.toString());
//         else respond(200, files.join("\n"));
//       });
//     else respond(200, fs.createReadStream(targetPath), mime.contentType(path.resolve(targetPath)));
//   });
// };

methods.DELETE = function (path, respond) {
  fs.stat(path, function (error, stats) {
    if (error && error.code == "ENOENT") respond(204);
    else if (error) respond(500, error.toString());
    else if (stats.isDirectory())
      fs.rmdir(path, respondErrorOrNothing(respond));
    else fs.unlink(path, respondErrorOrNothing(respond));
  });
};

function respondErrorOrNothing(respond) {
  return function (error) {
    if (error) respond(500, error.toString());
    else respond(204);
  };
}

methods.PUT = function (path, respond, request) {
  var outStream = fs.createWriteStream(path);
  outStream.on("error", function (error) {
    respond(500, error.toString());
  });
  outStream.on("finish", function () {
    respond(204);
  });
  request.pipe(outStream);
};

// make collection 
methods.MKCOL = function(path, respond, request) {
  
}

var server = http.createServer();

server.on("request", (request, response) => {
  function respond(code, body, type) {
    if (!type) type = "text/plain";
    response.writeHead(code, { "Content-Type": type });
    if (body && body.pipe) body.pipe(response);
    else response.end(body);
  }

  if (request.method in methods) {
    methods[request.method](urlToPath(request.url), respond, request)

  } else {
    respond(405, " Method " + request.method + " not allowed .");
  }
});

server.listen(port, () => {
  console.log("server listening on port", port);
});

function urlToPath(url) {
  var pathname = urlMod.parse(url).pathname;
  return "." + decodeURIComponent(pathname);
}
