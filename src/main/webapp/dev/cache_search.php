<?php
$result = file_get_contents('cache/search.json.gz');
$result = gzuncompress($result);
echo $result;
?>