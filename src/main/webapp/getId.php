<?php
	$ch = curl_init('http://beta.data-arc.org/api/getId?'.http_build_query($_GET));
	curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	$result = curl_exec($ch);
	curl_close($ch);
	echo $result;
?>