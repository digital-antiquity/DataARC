<?php
	$params = file_get_contents('php://input');
	$ch = curl_init('http://beta.data-arc.org/api/search');
	curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
	curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, array(
    'Content-Type: application/json',
    'Content-Length: ' . strlen($params))
	);
	$result = curl_exec($ch);
	curl_close($ch);
	if (!file_exists('cache')) mkdir('cache', 0777, true);
	file_put_contents('cache/search.json.gz', gzcompress($result));
	echo $result;

// $socket = fsockopen('beta.data-arc.org', 80, $errno, $errstr, 30);
// $json = file_get_contents('php://input');
// $http  = "POST /api/search HTTP/1.1\r\n";
// $http .= "Host: beta.data-arc.org\r\n";
// $http .= "Content-Type: application/json\r\n";
// $http .= "Content-Length: ".strlen($json)."\r\n";
// $http .= "Connection: close\r\n\r\n";
// $http .= $json;
// fwrite($socket, $http);
// $result = '';
// header('Content-type: application/json; charset=utf-8');
// while (!feof($socket)) {
// 	$result .= fgets($socket, 512);
// }
// fclose($socket);
// // $result = substr($result, strpos($result, "\r\n\r\n") + 4);
// // $result = json_encode($result);
// // file_put_contents('cache/search.json.gz', gzcompress($result));
// file_put_contents('cache/search.json', $result);
// echo $result;
