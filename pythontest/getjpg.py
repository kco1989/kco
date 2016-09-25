#coding=utf-8
import re
import urllib.request


def gethtml(url):
    page = urllib.request.urlopen(url)
    return page.read()


def getimg(html):
    reg = r'(http.*\.jpg)'
    imgre = re.compile(reg)
    return re.findall(imgre, html)

html = gethtml('http://tieba.baidu.com/p/4793005750')

print(html)
print(getimg(html))