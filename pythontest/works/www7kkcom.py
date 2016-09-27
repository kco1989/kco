from urllib import request
import re
import os
base_html = 'http://www.7kk.com'
searchPath = '/search?keyword='
keyword = u'美女'

yeshu = 20
path = "e:\\meinv\\美女"


def save_pic(pics):
    """保存图片"""
    for p in pics:
        fileName = path + os.sep + p.split("/")[-1]
        if not os.path.exists(fileName):
            with open(fileName, "wb") as fp:
                print("正在保存:", fileName)
                fp.write(request.urlopen(p).read())


def parse_hrefs():
    for i in range(1, yeshu + 1):
        # url = base_html + searchPath.replace('page', str(i))
        url = (base_html + searchPath + request.quote(keyword.encode("UTF-8"))) + '&page=' + str(i)
        print("解析:",url)
        page = request.urlopen(url)
        html = page.read().decode('UTF-8')
        hrefs = re.findall('a href="(/picture/.*?html)"', html, re.S)
        print("解析到的hrefs:", hrefs)
        parse_pics(hrefs)


def parse_pics(hrefs):
    for url in [base_html + item for item in hrefs]:
        page = request.urlopen(url)
        print("解析:", url)
        html = page.read().decode('UTF-8')
        # pics.extend(re.findall('a target="_blank" href="(.*?jpg)" class="downpic">下载原图</a>', html, re.S))
        pics = re.findall(r'<img src="(http[^\"]*?jpg)" alt=".*?">', html, re.S)
        print("解析的图片url:", pics)
        save_pic(pics)


def main():
    if not os.path.exists(path):
        os.mkdir(path)
    parse_hrefs()


if __name__ == '__main__':
    main()