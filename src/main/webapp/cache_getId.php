<?php
$result = file_get_contents('cache/getId.json.gz');
$result = gzuncompress($result);
echo $result;
?>