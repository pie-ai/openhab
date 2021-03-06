#include <SPI.h>
#include <Ethernet.h>

/***************************
 * configuration
 ***************************/

// state gets broadcasted every broadcastInterval ms
unsigned long lastBroadcast;
unsigned long broadcastInterval = 10000;

// network configuration, use generator http://www.miniwebtool.com/mac-address-generator/
byte mac[] = { 0x74,0x69,0x69,0x2D,0x30,0x31 };
// static or dynamic ip?
//#define NETWORK_STATIC
#define NETWORK_DYNAMIC

// broadcasting via udp or tcp (http based)
//#define MODE_UDP
#define MODE_TCP

// network via w5100 or enc28j60
#define NETWORK_W5100
//#define NETWORK_ENC28J60

// static ip address or fallback ip address if dhcp does not work
IPAddress staticIp(192, 168, 178, 177);
IPAddress staticGateway(192, 168, 178, 1);
IPAddress staticSubnet(255, 255, 255, 0);

// broadcasting settings:
#ifdef MODE_UDP
	#ifdef NETWORK_W5100
		IPAddress broadcastIp(255, 255, 255, 255);
		unsigned int protocolPort = 10666;
	#endif
#endif
// server related settings
#ifdef MODE_TCP
//IPAddress server(74,125,232,128);  // numeric IP for Google (no DNS)
char server[] = "192.168.178.21";    // name address for Google (using DNS)
char serverPath[] = "/netplug/";
int serverPort = 80;
// identification of device
char netplugId[] = "007";
// secret to sign requests
char serverSecret[] = "123";
#endif

// implementation specific
// enc28j60
#ifdef NETWORK_ENC28J60
	byte Ethernet::buffer[700];
#endif
// w5100
#ifdef NETWORK_W5100
  #ifdef MODE_UDP
	char packetBuffer[UDP_TX_PACKET_MAX_SIZE]; //buffer to hold incoming packet,
	EthernetUDP Udp;  
  #endif
  #ifdef MODE_TCP
    EthernetClient client;
  #endif
#endif

// relais
#define ON 0
#define OFF 255
int relaisCount = 0;
int relais[] = {};
int relaisState[] = {};

// relais as push button
int relaisAsPushButtonCount = 2;
int relaisAsPushButton[] = {6, 7};

// temparature
int temperatureSensorCount = 1;
int temperatureSensors[] = { A0 };

// digital ports (outputs)
int digitalPortCount = 0;
int digitalPorts[] = {   };
int digitalPortsState[] = {};
  
// analog inputs
int analogInputCount = 0;
int analogInputs[] = { };
  
/***************************
 * shared methods
 ***************************/

/**
 * changes a relais state
 * relaisId (<relaisCount)
 * state (ON/OFF)
 */
void changeRelaisState(int relaisId, int state)
{
	Serial.print("DEBUG: changeRelaisState(relaisId=");
	Serial.print(relaisId);
	Serial.print(",state=");
	Serial.print(state);
	Serial.println("):");
	
	if (relaisCount >= relaisId)
	{
		if (state == 0)
		{
			analogWrite(relais[relaisId], OFF);
			relaisState[relaisId] = OFF;
			broadcast();
		}
		else if (state == 1)
		{
			analogWrite(relais[relaisId], ON);
			relaisState[relaisId] = ON;
			broadcast();
		}
		else
		{
			Serial.print("ERROR: state=");
			Serial.print(state);
			Serial.print(" is invalid");
		}
	}
	else
	{
		Serial.print("ERROR: relaisId=");
		Serial.print(relaisId);
		Serial.print(" does not exist");
	}
}

/**
 * pushes (switch on, wait, switch off) a relais that is used as a push button
 */
void pushesRelaisAsPushButton(int relaisId)
{
	Serial.print("DEBUG: pushesRelaisAsPushButton(relaisId=");
	Serial.print(relaisId);
	Serial.println("):");
	
	if (relaisAsPushButtonCount >= relaisId)
	{
		analogWrite(relaisAsPushButton[relaisId], ON);
		delay(1000);
		analogWrite(relaisAsPushButton[relaisId], OFF);
	}
	else
	{
		Serial.print("ERROR: relaisId=");
		Serial.print(relaisId);
		Serial.print(" does not exist");
	}
}

/**
 * changes a digital port output
 * digitalPort (<digitalPortCount)
 * state (0-255)
 */
void changeDigitalPort(int digitalPort,int value)
{
	Serial.print("DEBUG: changeDigitalPort(digitalPort=");
	Serial.print(digitalPort);
	Serial.print(",value=");
	Serial.print(value);
	Serial.println("):");
	
	if (relaisCount >= digitalPort)
	{
		int tmp = value%255;
		analogWrite(digitalPorts[digitalPort], tmp);
		digitalPortsState[digitalPort] = tmp;
		broadcast();
	}
	else
	{
		Serial.print("ERROR: digitalPort=");
		Serial.print(digitalPort);
		Serial.print(" does not exist");
	}
}

/**
 * converts an ascii symbol to it's int representation:
 * 0-9 = 0-9
 * A-Z = 10 - 35
 */
int asciiToInt(char ascii)
{
	if (ascii >= 48 && ascii <= 57)
	{
		return ascii - 48;
	}
	else if (ascii >= 65 && ascii <= 90)
	{
		return ascii - 65 + 10;
	}
	else
	{
		Serial.print("ERROR: illegal ascii char '");
		Serial.print(ascii);
		Serial.println("'");
		return -1;
	}
}

/**
 * converts an ascii symbol to it's int representation:
 * 0-9 = 0-9
 * A-Z = 10 - 35
 */
String intToAsci(int i)
{
	String result = String(i, HEX);
	return result;
}

/**
 * handles command
 */
void handle(String message)
{
	Serial.print("DEBUG: handle message '");
	Serial.print(message);
	Serial.println("'");
	if (message.startsWith("S:"))
	{
		String command = message.substring(2,message.length());
		if (command.startsWith("R"))
		{
			// command = R0=1
			/*Serial.print("handling command:");
			Serial.println(command);*/
			
			// relais 
			int relais = 0;
			int state = 0;
			
			String relaisIdAsString = command.substring(1,2);
			char relaisAsChar = relaisIdAsString.charAt(0);
			relais = asciiToInt(relaisAsChar);
			
			String relaisStateAsString = command.substring(3,4);
			char relaisStateAsChar = relaisStateAsString.charAt(0);
			state = asciiToInt(relaisStateAsChar);
			
			changeRelaisState(relais,state);
		}
		else if (command.startsWith("P"))
		{
			// command = P0
						
			// relais 
			int relais = 0;
			
			String relaisIdAsString = command.substring(1,2);
			char relaisAsChar = relaisIdAsString.charAt(0);
			relais = asciiToInt(relaisAsChar);
						
			pushesRelaisAsPushButton(relais);
		}
		else if (command.startsWith("D"))
		{
			// command = D0=0 or D1=125 or D2=255
			
			// digital port
			int digitalPort = 0;
			int value = 0;
			
			String portIdAsString = command.substring(1,2);
			char portIdAsChar = portIdAsString.charAt(0);
			digitalPort = asciiToInt(portIdAsChar);
			
			String valueString = command.substring(3,command.length());
			for (int i=0;i<valueString.length();i++)
			{
				char valueAsChar = valueString.charAt(i);
				value = value * 16;
				
				int tmp = asciiToInt(valueAsChar);
				value = value + tmp;
			}
		
			// limit to o to 255:
			value = value % 256;
			changeDigitalPort(digitalPort,value);
		}
		else
		{
			Serial.print("ERROR: unknown command '");
			Serial.print(command);
			Serial.println("'"); 
		}
	}
	else
	{
		Serial.print("ignoring message:");
		Serial.println(message); 
	}	
}

#ifdef MODE_TCP
void pull()
{
	#ifdef NETWORK_W5100
		// if you get a connection, report back via serial:
		if (client.connect(server, serverPort)) 
		{
			Serial.print("DEBUG: connected to '");
			Serial.print(server);
			Serial.println("'");
			
			client.print("GET ");
			client.print(serverPath);
			client.println(" HTTP/1.1");
			client.print("Host: ");
			client.println(server);
			client.print("NetPlugId: ");
			client.println(netplugId);
			client.println("Command: GetCommand");
			client.println("Connection: close");
			client.println();
		
			int lineBreakCount = 0;
			int carriageReturnCount = 0;
			String message = "";
			while(client.connected())
			{
				while(client.available())
				{
					char c = client.read();
					
					// Serial.print(c);
					// Serial.print(" = ");
					// Serial.println(intToAsci(c));
					
					if (c == 13)
					{
						carriageReturnCount++;
					}
					else if (c == 10)
					{
						lineBreakCount++;
					}
					else if (lineBreakCount == 2 && carriageReturnCount == 2)
					{
						message+=c;
					}
					else
					{
						lineBreakCount = 0;
						carriageReturnCount = 0;
					}
				}
			}
			
			// message=NetPlugV1|csdcsdc
			String command = message.substring(10,message.length());
			Serial.print("DEBUG: command '");
			Serial.print(command);
			Serial.println("'");
			client.stop();
			handle(command);
		} 
		else 
		{
			Serial.print("ERROR: connecting to '");
			Serial.print(server);
			Serial.println("' failed");
		}
	#endif
	#ifdef NETWORK_ENC28J60
		Serial.println("ERROR: implement pull for enc28j60");
	#endif
}
#endif

void broadcast()
{
	// SR0=0,SR1=0,SD0=F0,SD1=F0,SD2=F0
	// NetPlugV1|B:D0=08,R0=1 
	// NetPlugV1|B:SR0=0,SR1=0,SD0=F0,SD1=F0,SD2=F0                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
  
	String reply = "NetPlugV1|B:";
	boolean payloadExists = false;
	for (int i=0;i<relaisCount;i++)
	{
		if (payloadExists)
		{
			reply += ",";
		}
		reply += "R";
		reply += i;
		reply+= "=";
		if (relaisState[i] == ON)
		{
			reply+= "1";
		}
		else
		{
			reply+= "0";
		}
		payloadExists = true;
	}

	for (int i=0;i<temperatureSensorCount;i++)
	{
		// 
		int pin = temperatureSensors[i];
		// Serial.print("reading analaog value from analog pin "); 
		// Serial.println(pin);
		
		//getting the voltage reading from the temperature sensor
		float reading = analogRead(pin);  
		// Serial.print(reading, DEC); 
		// Serial.println(" analog value");

		// converting that reading to voltage, for 3.3v arduino use 3.3
		float degrees = (reading * 5.0 * 100.0) / 1024.0;
		if (payloadExists)
		{
			reply += ",";
		}
		reply += "T";
		reply += i;
		reply+= "=";
		reply+=(byte)(degrees*100.0);
		payloadExists = true;
	}
  
	for (int i=0;i<digitalPortCount;i++)
	{
		if (payloadExists)
		{
			reply += ",";
		}
		
		reply += "D";
		reply += i;
		reply+= "=";
		reply+=intToAsci(digitalPortsState[i]);
		payloadExists=true;
	}

	for (int i=0;i<analogInputCount;i++)
	{
		if (payloadExists)
		{
			reply += ",";
		}
		reply += "A";
		reply += i;
		reply+= "=";
		reply+=intToAsci(analogRead(analogInputs[i]));
		payloadExists=true;
	}


	//reply = reply + "ST3=2112";
	Serial.print("DEBUG: broadcasting '");
	Serial.print(reply);
	Serial.println("'");


	#ifdef NETWORK_ENC28J60
		#ifdef MODE_UDP
			char replyArray[reply.length() + 1];
			reply.toCharArray(replyArray, reply.length() + 1);
			// sendUdp (char *data, uint8_t len, uint16_t sport, uint8_t *dip, uint16_t dport)
			char *data = replyArray;
			uint8_t len = reply.length();
			uint16_t sport = protocolPort;
			static byte destIp[] = { 255,255,255,255 };
			uint8_t *dip = destIp;
			ether.sendUdp(replyArray,reply.length(), protocolPort,destIp,protocolPort);
		#else
		todo implement enc28j60 tcp
	#endif
	#endif
	#ifdef NETWORK_W5100
		#ifdef MODE_UDP
			char replyArray[reply.length() + 1];
			reply.toCharArray(replyArray, reply.length() + 1);
			Udp.beginPacket(broadcastIp, protocolPort);
			Udp.write(replyArray);
			Udp.endPacket();
		#endif
		#ifdef MODE_TCP
			if (client.connect(server, serverPort)) 
			{
				Serial.print("DEBUG: connected to '");
				Serial.print(server);
				Serial.println("'");
				
				client.print("POST ");
				client.print(serverPath);
				client.println(" HTTP/1.1");
				client.print("Host: ");
				client.println(server);
				client.print("NetPlugId: ");
				client.println(netplugId);
				client.println("Command: AddUpdate");
				client.println("Connection: close");
				client.println("Content-Type: application/x-www-form-urlencoded");
				client.print("Content-Length: ");
				client.println(reply.length());
				client.println("");
				client.println(reply);
				
				client.println();
			
				int lineBreakCount = 0;
				int carriageReturnCount = 0;
				String command = "";
				while(client.connected())
				{
					while(client.available())
					{
						char c = client.read();
						
						// Serial.print(c);
						// Serial.print(" = ");
						// Serial.println(intToAsci(c));
						
						if (c == 13)
						{
							carriageReturnCount++;
						}
						else if (c == 10)
						{
							lineBreakCount++;
						}
						else if (lineBreakCount == 2 && carriageReturnCount == 2)
						{
							command+=c;
						}
						else
						{
							lineBreakCount = 0;
							carriageReturnCount = 0;
						}
					}
				}
				Serial.print("DEBUG: response '");
				Serial.print(command);
				Serial.println("'");
				client.stop();
			} 
		#endif
	#endif
	
	
	
	lastBroadcast = millis();
	
	return;
}

void setup () {
	Serial.begin(9600);
	Serial.println("DEBUG: setup()");
	#ifdef NETWORK_W5100
	  	#ifdef NETWORK_DYNAMIC
			if (Ethernet.begin(mac) == 0) {
				Serial.println("ERROR: DHCP failed");
				Serial.println("DEBUG: falling back to static configuration");
				Ethernet.begin(mac, staticIp, staticGateway, staticGateway, staticSubnet);
			}
			Serial.print("DEBUG: My IP:");
			for (byte thisByte = 0; thisByte < 4; thisByte++) {
				// print the value of each byte of the IP address:
				Serial.print(Ethernet.localIP()[thisByte], DEC);
				if (thisByte < 3)
				{
					Serial.print(".");
				}
			}
			Serial.println("");
			
	  	#endif
	  	#ifdef NETWORK_STATIC
			Ethernet.begin(mac,ip);
	  	#endif
	#endif
	#ifdef NETWORK_ENC28J60
		Serial.println("DEBUG: Ethernet.dhcpSetup");
		if (ether.begin(sizeof Ethernet::buffer, mac) == 0) 
		{
			Serial.println( "ERROR: Failed to access Ethernet controller");
		}
		if (!ether.dhcpSetup())
		{
			Serial.println("ERROR: DHCP failed");
		}
		ether.printIp("DEBUG: My IP: ", ether.myip);
		ether.printIp("Netmask: ", ether.mymask);
		ether.printIp("DEBUG: GW IP: ", ether.gwip);
		ether.printIp("DEBUG: DNS IP: ", ether.dnsip);
	#endif



#ifdef MODE_UDP
	Serial.println("DEBUG: Udp.begin");
	Udp.begin(protocolPort);
        #ifdef NETWORK_ENC28J60
	  ether.udpServerListenOnPort(&udpSerialPrint, protocolPort);
        #endif
#endif
  
	Serial.println("DEBUG: relais setup loop");
	for (int i=0;i<relaisCount;i++)
	{
		Serial.print("DEBUG: setting up relais ");
		Serial.print(i);
		Serial.print(" to pin ");
		Serial.println(relais[i]);
		pinMode(relais[i], OUTPUT);
		changeRelaisState(i,0);
	}
  
	Serial.println("DEBUG: relais as push button setup loop");
	for (int i=0;i<relaisAsPushButtonCount;i++)
	{
		Serial.print("DEBUG: setting up relais as push button ");
		Serial.print(i);
		Serial.print(" to pin ");
		Serial.println(relaisAsPushButton[i]);
		pinMode(relaisAsPushButton[i], OUTPUT);
		analogWrite(relaisAsPushButton[i], OFF);
	}  
  
  
	Serial.println("DEBUG: digital port setup loop");
	for (int i=0;i<digitalPortCount;i++)
	{
		pinMode(digitalPorts[i], OUTPUT);
	}
	Serial.println("DEBUG: setup");
}

void loop()
{
	if (millis() > lastBroadcast + broadcastInterval)
	{
		#ifdef MODE_TCP
		pull();
		#endif
		broadcast();
		lastBroadcast = millis();
	} 
	#ifdef MODE_UDP
		#ifdef NETWORK_W5100	
			int packetSize = Udp.parsePacket();
			if(packetSize)
			{
				// read the packet into packetBufffer
				Udp.read(packetBuffer,UDP_TX_PACKET_MAX_SIZE);

				String bufferContent = String(packetBuffer);
				// message = S:R0=1
				String message = bufferContent.substring(0,packetSize);
    
				Serial.print("DEBUG: Received packet of size ");
				Serial.println(packetSize);
				handle(message);
			}
		#endif
		#ifdef NETWORK_ENC28J60	
		
		#endif
	#endif
}