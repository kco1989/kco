import urllib.request
import re

base_html = "http://python.usyiyi.cn/documents/python_352/library/"
index_html = "index.html"
regstr = r'href="([^.]+\.html)"'


def gethtml():
    print(base_html + index_html)
    page = urllib.request.urlopen(base_html + index_html)
    return page.read().decode('UTF-8')

html = gethtml()
print(html)
reg = re.compile(regstr)
list = reg.findall(html)
print(list)