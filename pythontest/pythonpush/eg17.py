from urllib import request
from http import cookiejar

cookie = cookiejar()
opener = request.build_opener(request.HTTPCookieProcessor(cookie))
request.install_opener(opener)
req = request.Request(url)
content = request.urlopen(req)



