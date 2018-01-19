<?php
$result = file_get_contents('cache/topicmap.json.gz');
$result = gzuncompress($result);
echo $result;
?>