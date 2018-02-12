<?php
// $params = json_encode(array('idOnly'=>true, 'spatial'=>array('topLeft'=>array(-20.500488281250004, 65.2291018831922), bottomRight=>array(-24.49951171875, 66.52201581569871))));

// data needs to be POSTed to the url as JSON
$ch = curl_init('http://beta.data-arc.org/api/topicmap/view');
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

//execute post and close the connection
$result = curl_exec($ch);
curl_close($ch);
if (!file_exists('cache')) mkdir('cache', 0777, true);
file_put_contents('cache/topicmap.json.gz', gzcompress($result));
echo $result;
?>