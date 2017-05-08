# coding:utf-8

import urllib.request, re, string
import threading, queue, time
import sys

_DATA = []
FILE_LOCK = threading.Lock()
SHARE_Q = queue.Queue()         #构造一个不限制大小的的队列
_WORKER_THREAS_NUM = 3          #设置线程的个数

class MyThread(threading.Thread):

    def __init__(self, func):
        super(MyThread, self).__init__()
        self.func = func

    def run(self):
        self.func()

def worker():

    global SHARE_Q
    while not SHARE_Q.empty():
        url = SHARE_Q.get()
        my_page = get_page(url)
        find_title(my_page)
        time.sleep(1)
        SHARE_Q.task_done()


def get_page(url):
    """

    根据当前页码爬取网页HTML

    Args:
        cur_page: 表示当前所抓取的网站页码

    Returns:
        返回抓取到整个页面的HTML(unicode编码)

    Raises:
        URLError:url引发的异常
    """
    try:
        my_page = urllib.request.urlopen(url).read().decode("utf-8")
    except urllib.request.URLError as e:
        if hasattr(e, "code"):
            print("The server couldn't fulfill the request.")
            print("Error code: %s" % e.code)
        elif hasattr(e, "reason"):
            print("We failed to reach a server. Please check your url and read the Reason")
            print("Reason: %s" % e.reason)
    return my_page


def find_title(my_page):
    """

    通过返回的整个网页HTML, 正则匹配前100的电影名称


    Args:
        my_page: 传入页面的HTML文本用于正则匹配
    """
    temp_data = []
    movie_items = re.findall(r'<span.*?class="title">(.*?)</span>', my_page, re.S)
    for index, item in enumerate(movie_items):
        if item.find("&nbsp") == -1:
            temp_data.append(item)
    _DATA.extend(temp_data)

def main():
    global SHARE_Q
    threads = []
    douban_url = "http://movie.douban.com/top250?start={page}&filter=&type="
    for index in range(10):
        SHARE_Q.put(douban_url.format(page=index * 25))
    for i in range(_WORKER_THREAS_NUM):
        thread = MyThread(worker)
        thread.start()
        threads.append(thread)
    for thread in threads:
        thread.join()
    SHARE_Q.join()
    for page in _DATA:
        print(page)
    print("Spider Successful!!!")

if __name__ == '__main__':
    main()

