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
	file_put_contents('cache/search.json.gz', gzcompress($result));
	file_put_contents('cache/search.json', $result);
	echo $result;
?>