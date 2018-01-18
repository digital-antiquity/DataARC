<?php
$path = 'WEB-INF/content/';

// Get the index page
$src = file_get_contents($path.'about.ftl');

// Parse the source and load the include files
$src = preg_replace_callback('/<#include\s+"([^"]+)"[^>]*>/su', "load_file", $src);

// Parse and handle the replace sections
$src = preg_replace_callback('/<!--\s*REPLACE\s+"([^"]+)"[^-]*-->.*?<!--\s*\/REPLACE\s*-->/su', "load_file", $src);

echo $src;

function load_file($file) {
	global $path;
	return file_get_contents($path.$file[1]);
}
?>