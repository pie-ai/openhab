<?php
	// configuration
	$secret = "aaaa-bbbb-cccc-dddd";
	
	$dbUser = "netplug";
	$dbPass = "netplug";
	$dbUrl = "mysql:host=localhost;dbname=netplug";
	
	$dbPersistent = false;
	
	// defaul values
	$responseError = "NetPlugV1|E:";
	$responseEmpty = "NetPlugV1|S:";
	
	try {
		if ($dbPersistent)
		{
			$dbh = new PDO($dbUrl, $dbUser, $dbPass, array(PDO::ATTR_PERSISTENT => true));		
		}
		else
		{
			$dbh = new PDO($dbUrl, $dbUser, $dbPass);		
		}
	
		$headers = headers_list();
		$netplugId = "006";
		
		foreach (getallheaders() as $name => $value) {
			if ( $name == 'NetPlugId' )
			{
				$netplugId = $value;
			}
		}
		
		$sth = $dbh->prepare('SELECT * FROM netplug.devices WHERE netplug_id = :netplugId');
		$sth->bindParam(':netplugId', $netplugId, PDO::PARAM_STR, 64);
		$sth->execute();
		
		$netplug = $sth->fetchObject();
		
		if ($netplug == null)
		{
			die($responseError + "netplug not found");	
		}
		
		print_r($netplug);
		
		// die($netplugId);
		// var_dump(headers_list());
		//phpinfo();
		//echo 'S:P0=1';
		$dbh = null;
	}
	catch (PDOException $e)
	{
	    print "Error!: " . $e->getMessage() . "<br/>";
	    die();
	}
?>