#encoding=utf-8
import ostest
import time

source = [r'e:\test', r'e:\index']
target_dir = r'e:\\Backup'
target = target_dir + ostest.sep + time.strftime("%Y%m%d%H%M%S") + '.zip'
zip_command = "zip -qr {0} {1}".format(target, ' '.join(source))

if ostest.system(zip_command) == 0:
    print("Successful backup to", target)
else:
    print("Backup FAILED")