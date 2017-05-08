from urllib import request
import re

base_html = r"http://www.52tian.net"

page = request.urlopen(base_html)
html = page.read().decode('gbk')
pic_url = re.findall('img .*? d="(.*?)"', html, re.S)
print(pic_url)


for each in pic_url:
    print("下载图片" + each)
    name = each.split('/')[-1]
    page = request.urlopen(each)
    with open("./pic/" + name, "wb") as fp:
        fp.write(page.read())




