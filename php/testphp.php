<?php
$fiveMBs = 5 * 1024 * 1024;
$fp = fopen("php://temp//maxmemory: $fiveMBs ", "r+");

fputs($fp, "hello\n");
rewind($fp);

echo stream_get_contents($fp);

