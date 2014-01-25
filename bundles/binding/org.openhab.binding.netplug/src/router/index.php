<?php
	// configuration
	$secret = "aaaa-bbbb-cccc-dddd";
	
	$dbUser = "netplug";
	$dbPass = "netplug";
	$dbUrl = "mysql:host=localhost;dbname=netplug";
	
	$dbPersistent = false;
	
	// default values
	$responseError = "NetPlugV1|E:";
	$responseEmpty = "NetPlugV1|S:";
	
	/*
		everybody:
			get time
				* Command-Header: GetTime
		
		openhab:
			post command to netplug using
				* NetPlugId-Header
				* Command-Header: AddCommand
			get last status updates using
				* NetPlugId-Header
				* Command-Header: GetStatus
			
		netplug:
			post status updates using
				* NetPlugId-Header
				* Command-Header: AddUpdate
			get last commands using
				* NetPlugId-Header
				* Command-Header: GetCommand
	*/
	
	
	
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
		$netplugId = "";
		$command = "";
		
		foreach (getallheaders() as $name => $value) {
			if ( $name == 'NetPlugId' )
			{
				$netplugId = $value;
			} else if ( $name == 'Command' )
			{
				$command = $value;
			}
		}
		
		//error_log("command=$command for netplugId=$netplugId");
		
		$sth = $dbh->prepare('SELECT * FROM devices WHERE netplug_id = :netplugId');
		$sth->bindParam(':netplugId', $netplugId, PDO::PARAM_STR, 64);
		$sth->execute();
		
		$netplug = $sth->fetchObject();

		if ($netplug == null)
		{
			error_log("netplug (netplugId=$netplugId) was not found");
			print $responseError . "netplug not found";	
		}
		else
		{
			// error_log("netplug (netplugId=$netplugId) was found: ". print_r($netplug, 1));
		}
		
		
		
		$method = $_SERVER['REQUEST_METHOD'];
		if ($method == "GET")
		{
			if ("GetStatus" == $command)
			{
				$sth = $dbh->prepare('SELECT * FROM status WHERE device = :device AND retrieved_at IS NULL LIMIT 1');
				$sth->bindParam(':device', $netplug->device, PDO::PARAM_INT);
				$sth->execute();
				
				$status = $sth->fetchObject();
				if ($status == null)
				{
					error_log("GetStatus for netplugId=$netplugId: none");
					print $responseEmpty;
				}
				else
				{
					
					error_log("GetStatus for netplugId=$netplugId: ". $status->status);
					print $responseEmpty . $status->status;
					// status has been retrieved, marker it as retrieved
					$sth = $dbh->prepare('UPDATE status SET (retrieved_at) VALUES (now()) WHERE id = :id');
					$sth->bindParam(':id', $status->id, PDO::PARAM_INT);
					$sth->execute();
				}
							}
			else if ("GetCommand" == $command)
			{
				$sth = $dbh->prepare('SELECT * FROM commands WHERE device = :device AND executed_at IS NULL LIMIT 1');
				$sth->bindParam(':device', $netplug->id, PDO::PARAM_INT);
				$sth->execute();
				
				$command = $sth->fetchObject();
				if ($command == null)
				{
					error_log("GetCommand for netplugId=$netplugId: none");
					print $responseEmpty;
				}
				else
				{
					
					error_log("GetCommand for netplugId=$netplugId: " . $command->command);
					print $responseEmpty . $command->command;
					
					// command has been retrieved, marker it as executed
					$sth = $dbh->prepare('UPDATE commands SET executed_at=now() WHERE id = :id');
					$sth->bindParam(':id', $command->id, PDO::PARAM_INT);
					$sth->execute();
				}
				
			}
			else if ("GetTime" == $command)
			{
				print $responseEmpty . time();
			}
			else
			{
				error_log("illegal command=$command");
				print $responseError . "illegal command";	
			}
		}
		else
		{
			$body = file_get_contents('php://input');
			// error_log("post body: $body");
			if ("AddUpdate" == $command)
			{
				// all old status updates get deactivated
				$sth = $dbh->prepare('UPDATE status SET retrieved_at=0 WHERE device = :device;');
				$sth->bindParam(':device', $netplug->id, PDO::PARAM_INT);
				$sth->execute();
				
				error_log("AddUpdate for netplugId=$netplugId: $body");
				
				// insert status
				$sth = $dbh->prepare('INSERT INTO status (device, status, created_at) VALUES (:device,:status, now());');
				$sth->bindParam(':device', $netplug->id, PDO::PARAM_INT);
				$sth->bindParam(':status', $body, PDO::PARAM_STR, 64);
				$sth->execute();
				print $responseEmpty . "OK";
			}
			else if ("AddCommand" == $command)
			{
				$netPlugCommand = null;
				if (substr($body,0,strlen($responseEmpty)) == $responseEmpty)
				{
					$netPlugCommand = substr($body,strlen($responseEmpty));
				}
				else
				{
					$netPlugCommand = $body;
				}
				
				error_log("AddCommand for netplugId=$netplugId: $netPlugCommand");
				
				$sth = $dbh->prepare('INSERT INTO commands (device, command, created_at) VALUES (:device,:command, now());');
				$sth->bindParam(':device', $netplug->id, PDO::PARAM_INT);
				$sth->bindParam(':command', $netPlugCommand, PDO::PARAM_STR, 64);
				$sth->execute();
				print $responseEmpty . "OK";
			}
			else
			{
				error_log("illegal command=$command");
				print $responseError . "illegal command";	
			}
		}
		
		
		
		$dbh = null;
	}
	catch (PDOException $e)
	{
	    print "Error!: " . $e->getMessage() . "<br/>";
	    die();
	}
?>