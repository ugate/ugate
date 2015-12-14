<div class="body2">
	<div class="main">
<!-- content -->
		<section id="content">
			<div class="marg_top wrapper">
				<div class="box1_out">
					<div class="box1">
<!-- User Guide -->

						<!--<p></p>
						<h1>
							<font color="red">Under Construction (Last Update May 2012) Stay Tuned!
							</font>
						</h1>
						<p></p>
						<p></p>
						<table title="Preview Videos" cellpadding="3" cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<iframe
											src="http://www.gmodules.com/gadgets/ifr?url=http%3A%2F%2Fgoogle-code-project-hosting-gadgets.googlecode.com%2Fsvn%2Fbuild%2Fprod%2Fvideo%2FgcVideo.xml&amp;up_video=http%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DNYPrsTqbBkM&amp;parent=%2F%2Fcode.google.com%2Fhosting&amp;container=code"
											width="425" frameborder="0" height="344">
										</iframe>
									</td>
								</tr>
							</tbody>
						</table>
						<table title="Screenshots" cellpadding="3" cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/screen01.png">
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/screen02.png">
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/screen03.png">
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/screen04.png">
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/screen05.png">
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/screen06.png">
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h1>
							<a name="Summary:"></a>
							<u>Summary:</u>
							<a href="#Summary:" class="section_anchor"></a>
						</h1>
						<p></p>
						<p>
							<b>UGate is a fully configurable indoor/outdoor security system that:
							</b>
						</p>
						<ul>
							<li>
								<b>Takes pictures (w/night vision) and sends an email with the picture
									as an attachment to predefined recipient(s).</b>
								<ol>
									<li>Via alarm sensor(s) trip</li>
									<li>Via IR Remote Control</li>
									<li>Via GUI Application</li>
									<li>Via remote web interface (embedded web server in GUI Application)
									</li>
									<li>Via receiving an email with device commands (initiated from a
										computer, mobile phone, or any other device that can send
										emails/text messages).</li>
								</ol>
							</li>
							<li>
								<b>Remotely pan/tilt on-board camera.</b>
								<ol>
									<li>Via IR Remote Control</li>
									<li>Via GUI Application</li>
									<li>Via remote web interface (embedded web server in GUI Application)
									</li>
									<li>Via receiving an email with device commands (initiated from a
										computer, mobile phone, or any other device that can send
										emails/text messages).</li>
								</ol>
							</li>
							<li>
								<b>Remotely pan/tilt alarm sensor orientation.</b>
								<ol>
									<li>Via IR Remote Control (all sensors except microwave)</li>
									<li>Via GUI Application (all sensors- pan only for microwave)</li>
									<li>Via remote web interface (embedded web server in GUI Application)
									</li>
									<li>Via receiving an email with device commands (all sensors-
										initiated from a computer, mobile phone, or any other device that
										can send emails/text messages).</li>
								</ol>
							</li>
							<li>
								<b>Remotely open/close an entrance gate (optional).</b>
								<ol>
									<li>Via IR Remote Control</li>
									<li>Via GUI Application</li>
									<li>Via remote web interface (embedded in GUI Application)</li>
									<li>Via receiving an email with device commands (initiated from a
										computer, mobile phone, or any other device that can send
										emails/text messages).</li>
								</ol>
							</li>
						</ul>
						<p></p>-->
						<p></p>
						<table class="wikitable">
							<tbody>
								<tr>
									<td style="border: 1px solid #ccc; padding: 5px;">
										<table>
											<tbody>
												<tr>
													<th>
														<h2>Table of Contents</h2>
													</th>
												</tr>
												<tr>
													<td>
														<ol>
															<li>
																<a href="#Tools">Tools</a>
															</li>
															<li>
																<a href="#Bill_of_Materials">Bill of Materials</a>
															</li>
															<ol>
																<li>
																	<a href="#Hardware">Hardware</a>
																</li>
																<li>
																	<a href="#Supporting_Hardware">Supporting Hardware</a>
																</li>
																<li>
																	<a href="#Solar_Power_%28optional,_but_recommended%29">Solar Power (optional, but recommended)</a>
																</li>
																<li>
																	<a href="#Software">Software</a>
																</li>
															</ol>
															<li>
																<a href="#Getting_Started:">Getting Started:</a>
															</li>
															<ol>
																<li>
																	<a href="#IR_Receiver_Assembly">IR Receiver Assembly</a>
																</li>
																<li>
																	<a href="#Camera_Dome_Assembly">Camera Dome Assembly</a>
																</li>
																<li>
																	<a href="#Microwave_Motion/Speed_Sensor_Assembly">Microwave Motion/Speed Sensor Assembly</a>
																</li>
																<li>
																	<a href="#Sonar/PIR_Armature_Assembly">Sonar/PIR Armature Assembly</a>
																</li>
																<li>
																	<a href="#Circuit_Assembly_and_Wiring">Circuit Assembly and Wiring</a>
																</li>
															</ol>
															<li>
																<a href="#Controlling_Peripherals">Controlling Peripherals</a>
															</li>
															<ol>
																<li>
																	<a href="#The_Universal_Remote:">The Universal Remote:</a>
																</li>
																<ol>
																	<li>
																		<a href="#Programming_The_Universal_Remote">Programming The Universal Remote</a>
																	</li>
																	<li>
																		<a href="#Operating_The_Universal_Remote">Operating The Universal Remote</a>
																	</li>
																	<ol>
																		<li>
																			<a href="#Command_Codes">Command Codes</a>
																		</li>
																		<li>
																			<a href="#Controlling_The_Camera_Position">Controlling The Camera Position</a>
																		</li>
																		<li>
																			<a href="#Controlling_The_Sonar/PIR_Armature">Controlling The Sonar/PIR Armature</a>
																		</li>
																		<li>
																			<a href="#Controlling_The_Microwave_Position">Controlling The Microwave Position</a>
																		</li>
																		<li>
																			<a
																				href="#Controlling_The_Laser/IR_Receiver_%28Tripwire%29_Position">Controlling The Laser/IR Receiver (Tripwire) Position
																			</a>
																		</li>
																		<li>
																			<a href="#Controlling_The_Entrance_Gate">Controlling The Entrance Gate</a>
																		</li>
																	</ol>
																</ol>
																<li>
																	<a href="#UGate_GUI">UGate GUI</a>
																</li>
																<li>
																	<a href="#Web_Server">Embedded Web Server</a>
																</li>
																<li>
																	<a href="#Email">Email</a>
																</li>
																<li>
																	<a href="#iPhone_Setup">iPhone Setup</a>
																</li>
																<li>
																	<a href="#Android_Setup">Android Setup</a>
																</li>
															</ol>
														</ol>
													</td>
												</tr>
											</tbody>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
<!-- User Guide -->
					</div>
				</div>
				<div class="box1_bot"><div class="box1_bot_left"><div class="box1_bot_right"></div></div></div>
			</div>
		</section>
<!-- / content -->
	</div>
</div>
<div id="wikicontent" class="main wide">
						<p></p>
						<h1>
							<a name="Tools"></a>
							<u>Tools</u>
							<a href="#Tools" class="section_anchor"></a>
						</h1>
						<p></p>
						<ol>
							<li>Small snips or scissors</li>
							<li>Measuring Tape</li>
							<li>Electrical Tape</li>
							<li>Small flat-head and phillips screwdrivers</li>
							<li>Pliers</li>
							<li>Screws: 1/2 x 11/16 inch (QTY: 4 - used for camera dome base to
								6x6x4 junction box), 3/4 x 1/8 inch (QTY: 2 - used for microwave
								sensor to shield can), 2 and 1/4 inch deck screw (QTY: 1 - used as IR
								receiver/LED indicator hose hanger)</li>
							<li>Drill with 1/2 inch, 5/16 inch, 3/32 inch, and 11/64 inch drill
								bits along with a 1 and 1/4" hole-saw</li>
							<li>Dremel-ilke Rotary Tool with cutting disks</li>
							<li>Soldering iron w/solder and desoldering iron (or solder wick,
								solder pump)</li>
							<li>Mini Hot-Glue gun with glue sticks</li>
							<li>An outdoor silicone based caulk (squeeze tube or caulk gun)</li>
							<li>1 small can of CPVC Cement</li>
						</ol>
						<p></p>
						<h1>
							<a name="Bill_of_Materials"></a>
							<u>Bill of Materials</u>
							<a href="#Bill_of_Materials" class="section_anchor"></a>
						</h1>
						<h2>
							<a name="Hardware"></a>
							Hardware
							<a href="#Hardware" class="section_anchor"></a>
						</h2>
						<p></p>
						<ol>
							<li>
								<a href="http://www.digilentinc.com/Products/Detail.cfm?Prod=CHIPKIT-UNO32"
									rel="nofollow">Arduino chipKIT uno32</a>
								(Used to communicate with your computer and to control peripheral
								devices) ~$27
								<a href="http://octopart.com/partsearch#search/requestData&amp;q=UNO32"
									rel="nofollow">Multiple Suppliers</a>
							</li>
							<li>
								<a
									href="http://www.digi.com/products/wireless-wired-embedded-solutions/zigbee-rf-modules/point-multipoint-rfmodules/xbee-series1-module.jsp#overview"
									rel="nofollow">XBee</a>
								(Used for wireless communication from the UGate device to your
								computer... We used the Pro Series 1 60mW Chip Antenna w/~1 Mile
								Range, but
								<a href="http://octopart.com/partsearch#search/requestData&amp;q=XBee"
									rel="nofollow">any XBee</a>
								should work) x 2 @ $20-$30/each
								<a
									href="http://octopart.com/partsearch#search/requestData&amp;q=XBP24-ACI-001"
									rel="nofollow">Multiple Suppliers</a>
							</li>
							<li>
								<a href="http://adafruit.com/products/126" rel="nofollow">XBee Adapter</a>
								or
								<a
									href="http://www.droids.it/cmsvb4/content.php?143-990.001-XBee-Simple-Board"
									rel="nofollow">990.001 XBee Simple Board</a>
								(Used to connect an XBee to the Arduino UGate device and the other to
								connect an XBee to your computer) x 2 @ ~$8-$10/each
								<a href="http://adafruit.com/products/126" rel="nofollow">Adafruit</a>
							</li>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=USB%20to%20TTL%20UART%206PIN%20CP2102&amp;rvr_id=247324244981&amp;clk_rvr_id=247324244981"
									rel="nofollow">USB to TTL UART 6PIN CP2102</a>
								(Used for XBee Adapter to computer connection) ~$5/free shipping [
								<a
									href="http://shop.ebay.com/?_nkw=USB%20to%20TTL%20UART%206PIN%20CP2102&amp;rvr_id=247324244981&amp;clk_rvr_id=247324244981"
									rel="nofollow">EBay</a>
							</li>
							<li>
								<a
									href="http://www.seeedstudio.com/depot/electronic-brick-serial-ccd-camera-p-743.html"
									rel="nofollow">CCD UART Camera</a>
								or
								<a
									href="http://www.seeedstudio.com/depot/grove-serial-camera-p-945.html?cPath=144_152"
									rel="nofollow">alternative</a>
								(Used to take photos via universal remote, email, or using the GUI
								application) $35
								<a
									href="http://www.seeedstudio.com/depot/electronic-brick-serial-ccd-camera-p-743.html"
									rel="nofollow">Seeed Studio</a>
							</li>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=IR%20LED%20Board%20Plate&amp;rvr_id=247332209013&amp;clk_rvr_id=247332209013"
									rel="nofollow">IR LED Board Plate</a>
								(Used for night vision) ~$5/free shipping
								<a
									href="http://shop.ebay.com/?_nkw=IR%20LED%20Board%20Plate&amp;rvr_id=247332209013&amp;clk_rvr_id=247332209013"
									rel="nofollow">EBay</a>
							</li>
							<li>
								<a
									href="http://datasheet.octopart.com/P2N2222AG-ON-Semiconductor-datasheet-5458885.pdf"
									rel="nofollow">2N2222A NPN Switching transistors</a>
								(Used to turn power on/off night vision for camera and to pulse Laser
								tripwire) x 2 @ ~$0.55/each
								<a href="http://octopart.com/partsearch#search/requestData&amp;q=2N2222A"
									rel="nofollow">Multiple Suppliers</a>
							</li>
							<li>
								<a
									href="http://www.ebay.com/sch/i.html?_nkw=ultrasonic+sensor+module+waterproof"
									rel="nofollow">Waterproof Ultrasonic Sensor</a>
								(Used to detect movement and measure distance- any ultrasonic sensor
								will work, just make sure the range is far enough for your particular
								application (example uses ~11ft max). If used outdoors you will also
								need it to be weatherproof) ~$13/free shipping
								<a
									href="http://www.ebay.com/sch/i.html?_nkw=ultrasonic+sensor+module+waterproof"
									rel="nofollow">EBay</a>
							</li>
							<li>
								<a href="http://www.ebay.com/sch/i.html?_nkw=PIR+Module" rel="nofollow">PIR
									(Passive Infrared) Module</a>
								(Used to detect movement) ~$2/free shipping
								<a href="http://www.ebay.com/sch/i.html?_nkw=PIR+Module" rel="nofollow">EBay
								</a>
							</li>
							<li>
								<a
									href="http://www.parallax.com/Portals/0/Downloads/docs/prod/sens/32213-X-BandMotionDetector-v1.1.pdf"
									rel="nofollow">X-Band Microwave Module</a>
								(Used to detect movement and measure speed- any X-Band microwave
								module will work. You can even make your own from LNB satellite parts,
								a
								<a href="http://www.edparadis.com/radar/" rel="nofollow">hot wheels radar gun
								</a>
								,
								<a href="http://www.ebay.com/sch/i.html?_kw=microwave&amp;_kw=sensor"
									rel="nofollow">buy just the microwave module for ~$9</a>
								and interface the IF) ~$30
								<a
									href="http://octopart.com/partsearch#search/requestData&amp;q=32213+parallax"
									rel="nofollow">Multiple Suppliers</a>
							</li>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=mini%20servos&amp;rvr_id=247302233181&amp;clk_rvr_id=247302233181"
									rel="nofollow">Micro 9g 1.5kg/cm @ 4.8V Mini Servo Futaba Hitec HS-55 GWS
									walkera RC</a>
								(Used for pan/tilt of camera and sensors) x 5 @ ~$3/each [
								<a
									href="http://shop.ebay.com/?_nkw=mini%20servos&amp;rvr_id=247302233181&amp;clk_rvr_id=247302233181"
									rel="nofollow">EBay</a>
							</li>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=Ultra-Bright%20Tri-Color%20RGB%205mm%20LED&amp;rvr_id=247320381490&amp;clk_rvr_id=247320381490"
									rel="nofollow">Ultra-Bright Tri-Color RGB 5mm LED</a>
								(Used for IR receiver indicator) ~$1-$2/free shipping
								<a
									href="http://shop.ebay.com/?_nkw=Ultra-Bright%20Tri-Color%20RGB%205mm%20LED&amp;rvr_id=247320381490&amp;clk_rvr_id=247320381490"
									rel="nofollow">EBay</a>
							</li>
							<li>
								<a
									href="http://www.dailyshop.com/infrared-ir-laser-module-dot-5mw-980nm.html"
									rel="nofollow">5mW 980nm Infrared Laser Module</a>
								(Used for invisible tripwire) ~$7/free shipping
								<a
									href="http://www.dailyshop.com/infrared-ir-laser-module-dot-5mw-980nm.html"
									rel="nofollow">DailyShop.com</a>
							</li>
							<li>
								<a href="http://www.vishay.com/docs/83305/tsop4p.pdf" rel="nofollow">TSOP4P38
									or TSOP58P38 38kHz IR Receiver</a>
								(Used for invisible laser tripwire distance detection) ~$0.40
								<a href="http://octopart.com/partsearch#search/requestData&amp;q=TSOP4P38"
									rel="nofollow">Multiple Suppliers</a>
							</li>
							<li>
								<a href="http://www.vishay.com/docs/82090/tsop48xx.pdf" rel="nofollow">IR
									Receiver TSOP4840 or TSOP58440 @ 40kHz are best for the 12 bit Sony
									SIRC</a>
								, but
								<a href="http://www.vishay.com/docs/82090/tsop48xx.pdf" rel="nofollow">TSOP4836
									@ 36kHz</a>
								will also work and was chosen for testing (Used for RX control
								commands via Universal Remote- just about
								<a href="http://www.vishay.com/ir-receiver-modules/" rel="nofollow">any</a>
								IR receiver will work (like TSOP38238), but you may be better off
								staying away from 38kHz receivers to prevent interference with the
								laser tripwire) ~$0.50
								<a href="http://octopart.com/partsearch#search/requestData&amp;q=TSOP4840"
									rel="nofollow">Multiple Suppliers (TSOP4840 or TSOP58440)</a>
								or
								<a href="http://octopart.com/partsearch#search/requestData&amp;q=TSOP4836"
									rel="nofollow">Multiple Suppliers (TSOP4836)</a>
							</li>
							<li>
								<a
									href="http://shop.ebay.com/i.html?_nkw=Wireless+SPY+Camera+Pinhole+Nanny+CCTV+surveillance+CAM&amp;_sacat=0&amp;_sop=15&amp;_odkw=wireless+spy+camera&amp;_osacat=0&amp;_trksid=p3286.c0.m270.l1313"
									rel="nofollow">Mini wireless video camera</a>
								(OPTIONAL: an extra non-interfacing mounted camera for capturing
								video- placed in to camera dome) ~$10-$35 [
								<a
									href="http://shop.ebay.com/i.html?_nkw=Wireless+SPY+Camera+Pinhole+Nanny+CCTV+surveillance+CAM&amp;_sacat=0&amp;_sop=15&amp;_odkw=wireless+spy+camera&amp;_osacat=0&amp;_trksid=p3286.c0.m270.l1313"
									rel="nofollow">EBay</a>
							</li>
						</ol>
						<p></p>
						<h2>
							<a name="Supporting_Hardware"></a>
							Supporting Hardware
							<a href="#Supporting_Hardware" class="section_anchor"></a>
						</h2>
						<p></p>
						<ol>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=USB%20cable&amp;rvr_id=247331715968&amp;clk_rvr_id=247331715968"
									rel="nofollow">Typical USB mini Cable</a>
								(Used to connect XBee Adapter to PC) ~$0.01/each/free shipping EBay
							</li>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=jumper%20wires&amp;rvr_id=247321168997&amp;clk_rvr_id=247321168997"
									rel="nofollow">1-pin dual-male jumper wires- assorted lengths 75pcs pack</a>
								(Used for Arduino/Peripherals) $1-$3 EBay
							</li>
							<li>
								<a
									href="http://www.ebay.com/sch/i.html?_nkw=1+pin+dual-female+jumper+wire+300mm+50pcs+pack&amp;_sacat=See-All-Categories"
									rel="nofollow">1 pin dual-female jumper wire 300mm 50pcs pack</a>
								(Used for Arduino/Peripherals) ~$10/with shipping EBay
							</li>
							<li>
								<a
									href="http://shop.ebay.com/i.html?_nkw=Solderless+Breadboard&amp;_sacat=0&amp;_odkw=Breadboard&amp;_osacat=0&amp;_trksid=p3286.c0.m270.l1313"
									rel="nofollow">Small Solderless Breadboard</a>
								(Used for Arduino/Peripherals) ~$2/free shipping EBay
							</li>
							<li>
								<a
									href="http://www.ebay.com/sch/i.html?_from=R40&_trksid=m570.l2736&_nkw=Prototype+Paper+PCB+Universal+Experiment+Matrix+Circuit+Board"
									rel="nofollow">Prototype Paper PCB Board</a>
								(Used for PIR sensor mount) ~$0.01/each/free shipping EBay
							</li>
							<li>
								<a
									href="http://www.ebay.com/sch/?_nkw=Waterproof%20Outdoor%20Dome%20Shell%20Housing"
									rel="nofollow">Security Camera Waterproof Outdoor Dome Shell Housing</a>
								(Used for camera) ~$6/free shipping EBay
							</li>
							<li>
								<a
									href="http://shop.ebay.com/?_nkw=Universal%20Remote&amp;rvr_id=247326937190&amp;clk_rvr_id=247326937190"
									rel="nofollow">Universal Remote</a>
								(Used for controlling peripheral devices) ~$0.99/free shipping EBay
							</li>
							<li>
								<a
									href="http://www.homedepot.com/buy/electrical-electrical-boxes-conduit-fittings-conduit/southwire-ultratite-3-4-in-x-25-ft-nonmetallic-liquidtight-conduit-147448.html"
									rel="nofollow">3 and 1/2 inches of 3/4 inch diameter Liquid-Tight Electrical
									Conduit</a>
								(Used for IR receiver/LED indicator housing) ~$0.30-FREE if you ask
								for a sample
							</li>
							<li>
								<a
									href="http://www.google.com/search?q=Carlon+6+in.+x+4+in.Junction+Box&amp;safe=active#q=Carlon+6+in.+x+4+in.Junction+Box&amp;hl=en&amp;safe=active&amp;prmd=ivns&amp;source=univ&amp;tbm=shop&amp;tbo=u&amp;sa=X&amp;ei=lnAUTtOJIqbf0QGO4KytDg&amp;ved=0CEkQrQQ&amp;bav=on.2,or.r_gc.r_pw.&amp;fp=e88630cdc138baf3&amp;biw=1806&amp;bih=997"
									rel="nofollow">6 x 6 x 4 Junction Box</a>
								(Used a main device container) ~$11 Home Depot, Lowes, etc.
							</li>
							<li>
								<a
									href="http://www.google.com/search?q=Carlon+6+in.+x+4+in.Junction+Box&amp;safe=active#ds=pr&amp;pq=carlon+6+in.+x+4+in.junction+box&amp;hl=en&amp;cp=8&amp;gs_id=h&amp;xhr=t&amp;q=Carlon+4+in.+x+4+in.Junction&amp;pf=p&amp;sclient=psy-ab&amp;safe=active&amp;tbm=shop&amp;source=hp&amp;pbx=1&amp;oq=Carlon+4+in.+x+4+in.Junction+Box&amp;aq=f&amp;aqi=&amp;aql=&amp;gs_sm=&amp;gs_upl=&amp;bav=on.2,or.r_gc.r_pw.,cf.osb&amp;fp=479b2199ceb32d28&amp;biw=1807&amp;bih=1087"
									rel="nofollow">4 x 4 x 4 Junction Box</a>
								(Used as microwave container) ~$8 Home Depot, Lowes, etc.
							</li>
							<li>3/4 male NTP x female MT PVC swivel (Used for sonar/PIR armature) x
								3 @ ~$1-2/each Home Depot, Lowes, etc.</li>
							<li>
								<a href="http://ugate.googlecode.com/files/cam-shaft-wire.jpg" rel="nofollow">A
									wire necklace or 5" of 18 gauge wire</a>
								(Used as a wire shaft for the camera tilt servo) ~$0.40-$3 Michaels
								Arts &amp; Craft Store (necklace) or Home Depot/Lowes for wire
							</li>
							<li>
								<a
									href="http://www.takeherb.com/product.asp?PID=74461&amp;gdftrk=gdfV21061_a_7c270_a_7c710_a_7c74461"
									rel="nofollow">A 3.5 fl oz. Spice Jar</a>
								(Used for the IR receiver and LED) ~$0.60-$3 Online, Walmart, etc.
							</li>
							<li>
								<a
									href="http://www.conagrafoods.com/consumer/brands/getBrand.do?page=libbys"
									rel="nofollow">Aluminum "Chicken Vienna Sausage" Can</a>
								(Used for microwave directional shield) ~$0.40 off-brand Walmart, etc.
							</li>
							<li>
								<a
									href="http://www.homedepot.com/buy/halex-2-in-conduit-hanger-208485.html"
									rel="nofollow">2" Standard Conduit Hanger</a>
								(Used to hold microwave shield for panning) ~$0.70-$2 Home Depot,
								Lowes, etc.
							</li>
							<li>
								<a href="http://www.ebay.com/sch/i.html?_nkw=woven+acrylic" rel="nofollow">Woven
									Acrylic Fabric (at least 5 inch by 10 inch) with glue</a>
								(Used to protect the PIR/sonar armature- used some Loctite 495, but
								most water resistant vinyl glues will work) ~$1-$5/including shipping
								EBay (
								<a href="http://en.wikipedia.org/wiki/Acrylic_fiber" rel="nofollow">Acrylic
									Fabric</a>
								is typically used for outdoor marine awnings. There are also some
								<a href="http://en.wikipedia.org/wiki/Sailcloth" rel="nofollow">alternative
									materials</a>
								.)
							</li>
						</ol>
						<p></p>
						<h2>
							<a name="Solar_Power_(optional,_but_recommended)"></a>
							Solar Power (optional, but recommended)
							<a href="#Solar_Power_%28optional,_but_recommended%29" class="section_anchor"></a>
						</h2>
						<p></p>
						<ol>
							<li>
								<a
									href="http://octopart.com/partsearch#search/requestData&amp;q=OKI-78SR-5%2F1.5-W36-C"
									rel="nofollow">OKI-78SR-5/1.5-W36-C DC/DC Converter</a>
								(Used for 12v battery to Arduino 5V VIN power)] ~$4
								<a
									href="http://octopart.com/partsearch#search/requestData&amp;q=OKI-78SR-5%2F1.5-W36-C"
									rel="nofollow">Multiple Suppliers</a>
								<font size="1">
									NOTE: The Arduino has a linear voltage regulator that can handle the
									12v from the battery, but that would waste a lot of battery power and
									cause the on-board heat sink to get very hot! (converter is up to
									90.5% efficiency vs. on-board linear regulator are typically rated at
									15-50% efficiency
									<a href="http://www.onsemi.com/PowerSolutions/product.do?id=NCP1117"
										rel="nofollow">NCP1117</a>
									or
									<a href="http://www.national.com/ds/LM/LM1117.pdf" rel="nofollow">LM1117</a>
									)
								</font>
							</li>
							<li>
								<a href="http://www.ebay.com/sch/i.html?_nkw=10w+12v+solar+panel"
									rel="nofollow">12V 10W Solar Panel</a>
								(Used as renewable power source) ~$35/free shipping EBay (Example uses
								Lotus model LTSP10)
							</li>
							<li>
								<a href="http://www.ebay.com/sch/i.html?_nkw=ub1250" rel="nofollow">12V 5A
									Battery</a>
								(Used as power source) ~$16/free shipping EBay
							</li>
							<li>
								<a
									href="http://www.ebay.com/sch/i.html?_nkw=12V+5A+Solar+Panel+Charge+Regulator+Controller+Battery"
									rel="nofollow">12V 5A Solar Panel Charge Regulator</a>
								(Used to charge the battery from the solar panel) ~$14/free shipping
								EBay
							</li>
						</ol>
						<p></p>
						<h2>
							<a name="Software"></a>
							Software
							<a href="#Software" class="section_anchor"></a>
						</h2>
						<p></p>
						<ol>
							<li>
								<a href="https://github.com/chipKIT32/chipKIT32-MAX/downloads" rel="nofollow">Arduino
									IDE (for chipKIT)</a>
								FREE
							</li>
							<li>
								<a href="http://code.google.com/p/xbee-arduino/" rel="nofollow">xbee-arduino
									(Arduino Library)</a>
								FREE
							</li>
							<li>
								<a href="http://code.google.com/p/xbee-api/" rel="nofollow">xbee-api (Java
									Library)</a>
								FREE
							</li>
							<li>
								<a href="http://www.java.com/en/" rel="nofollow">Java (Java Runtime
									Installation- update to the latest version in order to run JavaFX)
								</a>
								FREE
							</li>
							<li>
								<a href="http://javafx.com/" rel="nofollow">JavaFX (JavaFX 2.0 or greater-
									Java-based UI platform used by the UGate GUI)</a>
								FREE
							</li>
							<li>
								<a href="http://code.google.com/p/ugate/downloads/list" rel="nofollow">UGate
									GUI (Java/JavaFX 2.0 based GUI for communications to/from Arduino)
								</a>
								FREE
							</li>
							<li>
								<a href="http://code.google.com/p/ugate/downloads/list" rel="nofollow">UGate
									Arduino Sketch (PDE/INO)</a>
								FREE
							</li>
						</ol>
						<p></p>
						<h1>
							<a name="Getting_Started:"></a>
							<u>Getting Started:</u>
							<a href="#Getting_Started:" class="section_anchor"></a>
						</h1>
						<p></p>
						<p>
							The following step-by-step procedures will help guide you to setup,
							configure, and deploy your security and/or gating system. Although it's
							helpful to have some general knowledge of the
							<a href="http://www.arduino.cc/" rel="nofollow">Arduino</a>
							it's not required for you to know how to program the Arduino using the
							<a href="http://arduino.cc/en/Reference/HomePage" rel="nofollow">Arduino
								Programming Laguage</a>
							(based on the C-based
							<a href="http://wiring.org.co/" rel="nofollow">Wiring language</a>
							) unless you plan on changing how the hardware responds to the attached
							peripherals. Like any Arduino
							<i>sketch</i>
							(PDE/INO) file the
							<a href="http://code.google.com/p/ugate/downloads/list" rel="nofollow">ugate
								sketch</a>
							is easily compiled and uploaded to your Arduino using the
							<a href="http://arduino.cc/en/Main/Software" rel="nofollow">Arduino IDE</a>
							with a click of a button. Similarly, it's not required for you to know
							how to program in
							<a
								href="http://www.oracle.com/technetwork/java/javase/documentation/index.html"
								rel="nofollow">Java</a>
							or
							<a href="http://javafx.com/" rel="nofollow">JavaFX</a>
							unless you plan on changing the source code for the GUI interaction
							between your computer and the Arduino. The provided
							<a href="http://code.google.com/p/ugate/downloads/list" rel="nofollow">ugate.jar
							</a>
							can be ran just like any other executable file. If you adhere to the
							following instructions you wont need any prior electronics knowledge.
						</p>
						<p></p>
						<h2>
							<a name="IR_Receiver_Assembly"></a>
							<u>IR Receiver Assembly</u>
							<a href="#IR_Receiver_Assembly" class="section_anchor"></a>
						</h2>
						<p></p>
						<table title="IR Reciever Assembly" cellpadding="3" cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/ir1.jpg">
											<p>
												<b>Step 1:</b>
												Determine how to wire the IR receiver pins. Here we used a spare
												8-pin header and soldered the pins to the pins of the IR receiver
												so we can easily change the jumper cables out as needed. One
												advantage to this approach that we will see later is that it fits
												nicely in the electrical flex tubing. A 3 color LED has also been
												soldered to some of the remaining header pins that will be used to
												indicate whether or not keys entered using the universal remote
												are being received. We used a 3 color LED to indicate when a
												remote control command is accepted (green), rejected (red),
												executing (blue), or access keys are being entered (white).
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/ir2.jpg">
											<p>
												<b>Step 2:</b>
												We need to have a durable see-through enclosure to house our IR
												receiver. The option chosen here is to use a typical spice jar.
												The spice jars length is long enough to prevent the camera dome
												from getting in the way of our receiver and the diameter allows a
												snug fit for the flexible electrical tube to fit in.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/ir3.jpg">
											<p>
												<b>Step 3:</b>
												Next, determine the location where the camera dome and IR receiver
												will be located relative to the junction box. Mark the spot where
												the hole will be drilled for the IR receiver with a pencil.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/ir4.jpg">
											<p>
												<b>Step 4:</b>
												Drill a hole with a 1 and 1/4 inch hole saw for the IR receiver
												enclosure. Be careful to have a clean cut so that when we caulk
												the spice jar in place it will create a nice seal.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/ir5.jpg">
											<p>
												<b>Step 5:</b>
												Using an outdoor silicone based caulk... caulk the exterior seam
												between the junction box and the spice jar. Note: Squeeze tubes
												are available if you do not have access to a caulk gun.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/ir06.jpg">
											<p>
												<b>Step 6:</b>
												Also caulk the interior seam between the junction box and the
												spice jar. This will ensure a watertight seal for use outdoors.
												Allow to dry per specifications on the caulk tube (typically 2-4
												hours).
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/ir7.jpg">
											<p>
												<b>Step 7:</b>
												Next, cut a 3 and 1/2 inch portion of a 3/4 inch diameter
												electrical flex tube (if your nice to the people at your local
												hardware store you can get this piece for free ;). The tube will
												be used to hold the IR receiver in place so it isn't dangling
												around in the spice jar. It also makes the project look nicer
												because the wires aren't exposed inside the jar. You may have
												noticed that we also used some electrical tape over the
												header/pins... this of course is optional.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/ir8.jpg">
											<p>
												<b>Step 8:</b>
												Take a 2 and 1/4 inch screw (or a size that fits, but doesn't take
												up too much space) and drive it through the tubing close to one of
												the ends. This will keep the tubing from falling down to the
												bottom of the spice jar.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/ir9.jpg">
											<p>
												<b>Step 9:</b>
												Place the tubing into the spice jar to ensure the screw, tubing
												and caulk seam fit correctly. Remove any excess caulk if any of it
												is in the way.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/ir10.jpg">
											<p>
												<b>Step 10:</b>
												Run the wire/jumper cables that were previously soldered to the
												8-pin header through the opposite end of the screw into the flex
												tubing until the header pin is no longer exposed (you may have to
												gently squeeze the tubing to place the header inside it). Place
												the tubing back into the spice jar. Now, if you ever need to
												replace any of the parts used in the spice jar you simply lift the
												screw/tubing out.
											</p>
									</td>
									<td>
									</td>
									<td>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h2>
							<a name="Camera_Dome_Assembly"></a>
							<u>Camera Dome Assembly</u>
							<a href="#Camera_Dome_Assembly" class="section_anchor"></a>
						</h2>
						<p></p>
						<table title="Camera Dome Assembly" cellpadding="3" cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 1:</b>
												The camera comes in an enclosure that we will have to disassemble
												in order to fit it in the camera dome. The good news is that the
												camera dome has a bracket that is just the right size to screw the
												camera circuit to!
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam2.jpg">
											<p>
												<b>Step 2:</b>
												Using a small flat-head screwdriver gently pry open the enclosure
												at the seam.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam3.jpg">
											<p>
												<b>Step 3:</b>
												Inside the camera enclosure you will see the camera circuit with
												wires that are hot-glued to prevent them from detaching from the
												board. Using a screwdriver or small scissors detach the wires from
												the hot-glue being careful not to sever the wires or break the
												solder that holds the wire to the board. You may have to snip part
												of the plastic where the wires enter the enclosure in order to get
												a good angle to cut the glue away. Also, it may be helpful to
												remove the board from the sticky backing that holds the board to
												the enclosure so you can get underneath the glue to cut it.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam4.jpg">
											<p>
												<b>Step 4:</b>
												Now that the camera circuit has been removed we are ready to place
												it in the camera dome. Twist the clear dome until you can lift the
												dome off of the base. Unscrew the black bracket and remove it from
												the base.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam5.jpg">
											<p>
												<b>Step 5:</b>
												The brass insert needs to be removed so the servo can be placed in
												it. The easiest way to do this is to take a 5/16 inch drill bit
												and drill a hole centering it on the brass insert. The brass
												insert is very soft so it will be reduced to shavings.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam6.jpg">
											<p>
												<b>Step 6:</b>
												Once drilled ensure the round servo horn (came with your servo)
												can fit with plenty of room without touching the sides of the
												base. The horn should be placed face-down relative to the side
												that connects to the servo.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam7.jpg">
											<p>
												<b>Step 7:</b>
												To make space for the servo to be inserted into the bottom of the
												base we need to completely remove the nub so the center hole is
												flush with the rest of the base. To do this use a 1/2 inch drill
												bit and center it on the hole we drilled on the other side.
												Periodically place the servo gear through the hole on the bottom
												of the base where you are drilling to see if it can reach the horn
												connection on the other side where we drilled the first hole. If
												you get a good connection you can stop drilling.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam8.jpg">
											<p>
												<b>Step 8:</b>
												Flip the base over and place the round horn connection into the
												servo gear. Don't worry if your connection is really loose- this
												is what we want.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam9.jpg">
											<p>
												<b>Step 9:</b>
												Your servo should have came with a black rubber ring and a small
												black screw used to secure the horn to the servo. Place the rubber
												ring into the camera dome bracket where the original screw used to
												be. Now place the servo screw into the rubber ring and screw it
												into the horn/servo (Tip: using the large hole on the top of the
												camera dome bracket makes it easier to screw it onto the servo).
												Again, we want the assembly to be very loose between the camera
												dome base and the bracket/horn/servo mount. When it gets secured
												to the junction box it wont seem as loose.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam10.jpg">
											<p>
												<b>Step 10:</b>
												Place the camera dome assembly on top of the junction box next to
												the spice jar and center it so the edges of the camera dome base
												do not overlap the edges of the junction box. The long side of the
												servo should be facing the front of the junction box and the gear
												side of the servo should be facing the opposite side of the spice
												jar. Mark the rectangular edges of the servo on the junction box
												with a marker/pen/pencil. Disassemble.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam11.jpg">
											<p>
												<b>Step 11:</b>
												Being careful not to cut the long side too far (needed to screw in
												the servo), cut the rectangular hole for the servo with rotary
												tool cutting disk. For better results you may want to use a 1/2
												inch drill bit to drill out the four corners before using the
												rotary tool. Using the silver screws that came with the servo,
												mount the screws to the servo inside the junction box (you may
												have to drill a pilot hole).
												<b>IMPORTANT: Make sure the the servo orientation is such that the
													gears are facing the opposite side from the spice jar (as seen in
													the photo). The gear should be gently rotated counterclockwise
													until it will no longer turn. This is a zero degree angle that
													needs to be set in order for the remote and GUI controls to be in
													the proper orientation or the controls will be reversed!</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam12.jpg">
											<p>
												<b>Step 12:</b>
												Next, center the camera dome base on the mounted servo gear that
												is sticking out of the junction box and push the round servo horn
												over the servo gear trying not to have the sides of the horn
												touching the camera dome base. Make sure that one of the two
												larger holes in the camera dome base is facing the back of the
												junction box (they will be used to run the tilt servo wires and
												the camera wires and will need to be in the back to keep them out
												of the way while the camera is panning). Use the 11/64 inch drill
												bit to drill the four holes that will secure the camera dome base
												to the junction box. Screw in the four 1/2 inch x 11/64 screws
												into the camera dome base to the junction box. Also, use the 1/2
												inch drill to expand the larger hole in the back to the junction
												box where the wires will be ran. Caulk around the perimeter of the
												camera dome base where it joins the junction box using the outdoor
												caulk to create a weather-tight seal.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam13.jpg">
											<p>
												<b>Step 13:</b>
												Now we need to assemble the tilt servo. First we need to glue the
												tilt servo to the top portion of the camera dome bracket. Unscrew
												the top black mount from the bottom bracket base that connects to
												the camera dome base. The servo should be placed on the opposite
												side from the four screws that the camera circuit mounts to (where
												the large round hole is). The servos center point should be inline
												with the hole for the screw in the top bracket. This means the
												gear will be slightly offset from the hole for the screw on the
												bracket- this is what we want. Use a hot-glue gun to glue the
												servo to the top bracket (a generous amount of glue!). You may
												also want to add glue on the opposite side around the perimeter of
												the large hole.
												<b>IMPORTANT: Make sure the the servo horn arm is facing toward
													the top (as seen in the photo) and the gear is in it's farthest
													position toward the top. This can be accomplished by gently
													rotating the horn until it will no longer rotate toward the top
													position. This is a zero degree angle that needs to be set in
													order for the remote and GUI controls to be in the proper
													orientation or the controls will be reversed!</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam15.jpg">
											<p>
												<b>Step 14:</b>
												Using the single arm servo horn, the two arm servo horn, and the
												two silver mounting screws that came with the servo we can make a
												lever arm for tilting the camera. Place the single arm horn onto
												the servo we just glued on, but do NOT use the small black screw
												that came with the servo to secure it (ignore image for the
												moment). Take the two arm horn and one of the silver mounting
												screws and screw it into the last hole in the two arm horn and
												through the last hole in the single arm horn. Make sure the two
												arm horn has the servo gear connection facing away from the
												bracket so it doesn't get in the way of the lever assembly. Using
												a 3/32 inch drill bit, drill a hole on the side of the bracket
												base toward the bottom closest to the servo. Use the other silver
												screw to connect the two arm horn through the last hole in the arm
												and into the hole we just drilled in bracket base. Do not over
												tighten any of the screws. Mount the camera circuit that we
												removed from the enclosure to the bracket using the four screws
												that came in the bracket.
												<b>NOTE: Wires from the camera circuit should be on the bottom
													(contrary to the photo) unless you are mounting the camera on the
													top of the junction box.</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam16.jpg">
											<p>
												<b>Step 15:</b>
												Now That we have both the pan and tilt servos in place we need to
												calibrate their positioning to the desired angle of operation. To
												do so we need to do a preliminary setup to test out what we have
												done so far. Unscrew the screws that attach the top bracket to the
												bracket base and carefully bend the tilt lever back to expose the
												hole in the bracket base. Using the rubber ring and screw
												described in the previous steps, secure the bracket base to the
												round panning servo horn. Twist the camera and tilting servo wires
												and place them through the 1/2 hole we drilled in the junction
												box.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam-servo-cal.jpg">
											<p>
												<b>Step 16:</b>
												We are now ready to setup our calibration test. Download the
												Arduino IDE (for chipKIT) from
												<a href="https://github.com/chipKIT32/chipKIT32-MAX/downloads"
													rel="nofollow">here</a>
												and follow the directions for installation. Once installed open
												the Arduino application. It will give you a blank sketch. Copy the
												following code and paste it into the sketch. This code basically
												just tilts the camera up and down, pans the cam left to right, and
												repeats the process indefinitely. It has a slight delay that will
												allow you to push the camera mount to the left or right to
												establish the correct panning span required for your particular
												project. Use the diagram above to wire your Arduino accordingly.
												Connect your USB cable from the Arduino to your computer and click
												on the Upload button at the top of the Arduino IDE. Wait until it
												says "Done uploading..." at the bottom and you should see the
												camera start to pan/tilt.
											</p>
<!-- http://www.palfrader.org/code2html/code2html.html -->
<pre>
<font color="0000ff"><strong>#include <font color="#008000">&lt;Servo.h&gt;</font></strong></font>
<font color="#2040a0">Servo</font> <font color="#2040a0">tiltServo</font><font color="4444FF">;</font>
<font color="#2040a0">Servo</font> <font color="#2040a0">panServo</font><font color="4444FF">;</font>
<strong>int</strong> <font color="#2040a0">pos</font><font color="4444FF">;</font>
<strong>void</strong> <font color="#2040a0">setup</font><font color="4444FF">(</font><font color="4444FF">)</font> <font color="4444FF"><strong>{</strong></font><font color="4444FF"><strong>}</strong></font>
<strong>void</strong> <font color="#2040a0">loop</font><font color="4444FF">(</font><font color="4444FF">)</font> <font color="4444FF"><strong>{</strong></font>
  <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><strong>true</strong>, <font color="#FF0000">8</font>, <font color="#2040a0">panServo</font>, <strong>true</strong><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">1000</font><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><strong>false</strong>, <font color="#FF0000">9</font>, <font color="#2040a0">tiltServo</font>, <strong>false</strong><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">1000</font><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><strong>true</strong>, <font color="#FF0000">9</font>, <font color="#2040a0">tiltServo</font>, <strong>false</strong><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">1000</font><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><strong>false</strong>, <font color="#FF0000">8</font>, <font color="#2040a0">panServo</font>, <strong>true</strong><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">1000</font><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><strong>false</strong>, <font color="#FF0000">9</font>, <font color="#2040a0">tiltServo</font>, <strong>false</strong><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">1000</font><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><strong>true</strong>, <font color="#FF0000">9</font>, <font color="#2040a0">tiltServo</font>, <strong>false</strong><font color="4444FF">)</font><font color="4444FF">;</font>
  <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">1000</font><font color="4444FF">)</font><font color="4444FF">;</font>
<font color="4444FF"><strong>}</strong></font>
<strong>void</strong> <font color="#2040a0">rotateServo</font><font color="4444FF">(</font><font color="#2040a0">boolean</font> <font color="#2040a0">forward</font>,
    <strong>int</strong> <font color="#2040a0">pin</font>, <font color="#2040a0">Servo</font> <font color="#2040a0">servo</font>, <font color="#2040a0">boolean</font> <font color="#2040a0">full</font><font color="4444FF">)</font> <font color="4444FF"><strong>{</strong></font>
  <font color="#2040a0">servo</font>.<font color="#2040a0">attach</font><font color="4444FF">(</font><font color="#2040a0">pin</font><font color="4444FF">)</font><font color="4444FF">;</font>
  <strong>int</strong> <font color="#2040a0">s</font> <font color="4444FF">=</font> <font color="#2040a0">full</font> ? <font color="#FF0000">0</font> <font color="4444FF">:</font> <font color="#FF0000">15</font><font color="4444FF">;</font>
  <strong>int</strong> <font color="#2040a0">e</font> <font color="4444FF">=</font> <font color="#2040a0">full</font> ? <font color="#FF0000">180</font> <font color="4444FF">:</font> <font color="#FF0000">160</font><font color="4444FF">;</font>
  <strong>if</strong> <font color="4444FF">(</font><font color="#2040a0">forward</font><font color="4444FF">)</font> <font color="4444FF"><strong>{</strong></font>
    <strong>for</strong> <font color="4444FF">(</font><font color="#2040a0">pos</font> <font color="4444FF">=</font> <font color="#2040a0">s</font><font color="4444FF">;</font> <font color="#2040a0">pos</font> <font color="4444FF">&lt;</font> <font color="#2040a0">e</font><font color="4444FF">;</font> <font color="#2040a0">pos</font> <font color="4444FF">+</font><font color="4444FF">=</font> <font color="#FF0000">1</font><font color="4444FF">)</font> <font color="4444FF"><strong>{</strong></font>
      <font color="#2040a0">servo</font>.<font color="#2040a0">write</font><font color="4444FF">(</font><font color="#2040a0">pos</font><font color="4444FF">)</font><font color="4444FF">;</font>
      <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">7</font><font color="4444FF">)</font><font color="4444FF">;</font>
    <font color="4444FF"><strong>}</strong></font>
  <font color="4444FF"><strong>}</strong></font> <strong>else</strong> <font color="4444FF"><strong>{</strong></font>
    <strong>for</strong> <font color="4444FF">(</font><font color="#2040a0">pos</font> <font color="4444FF">=</font> <font color="#2040a0">e</font><font color="4444FF">;</font> <font color="#2040a0">pos</font><font color="4444FF">&gt;</font><font color="4444FF">=</font><font color="#2040a0">s</font><font color="4444FF">;</font> <font color="#2040a0">pos</font><font color="4444FF">-</font><font color="4444FF">=</font><font color="#FF0000">1</font><font color="4444FF">)</font> <font color="4444FF"><strong>{</strong></font>
      <font color="#2040a0">servo</font>.<font color="#2040a0">write</font><font color="4444FF">(</font><font color="#2040a0">pos</font><font color="4444FF">)</font><font color="4444FF">;</font>
      <font color="#2040a0">delay</font><font color="4444FF">(</font><font color="#FF0000">7</font><font color="4444FF">)</font><font color="4444FF">;</font>
    <font color="4444FF"><strong>}</strong></font>
  <font color="4444FF"><strong>}</strong></font>
  <font color="#2040a0">servo</font>.<font color="#2040a0">detach</font><font color="4444FF">(</font><font color="4444FF">)</font><font color="4444FF">;</font>
<font color="4444FF"><strong>}</strong></font>
</pre>
											<p></p>
									</td>
									<td>
										<iframe
											src="http://www.gmodules.com/gadgets/ifr?url=http%3A%2F%2Fgoogle-code-project-hosting-gadgets.googlecode.com%2Fsvn%2Fbuild%2Fprod%2Fvideo%2FgcVideo.xml&amp;up_video=http%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DetdJuvhWalM&amp;parent=%2F%2Fcode.google.com%2Fhosting&amp;container=code"
											width="308" frameborder="0" height="231">
										</iframe>
										<p>
											<b>Step 17:</b>
											You may have to adjust the tilting single arm horn so that it is
											tilting properly by taking it off the servo gear and moving it up
											or down a notch or two. Unplug the USB cable, do the adjustment,
											and plug the USB cable back in. Do this until tilting occurs
											similar to the video above. Also, we may need to push the camera
											left or right to get the panning angle span you desire (this can be
											done while the USB is plugged in). For instance, we don't want the
											camera to pan all the way in front of the IR spice jar enclosure.
											You may want it to pan farther to the side opposite to the IR
											enclosure? Once the tilt and pan is calibrated to the desired
											angles we are ready to hot-glue the round servo horn to the bracket
											base (NOTE: make sure when you unplug the USB cable that you do not
											move the bracket to the left or right or your pan calibration will
											not be locked into place).
											<b>NOTE: Wires from the camera circuit should be on the bottom
												(contrary to the video) unless you are mounting the camera on the
												top of the junction box.</b>
										</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam17.jpg">
											<p>
												<b>Step 18:</b>
												Repeat the same procedure we performed in
												<b>Step 15</b>
												where we unscrewed the two top bracket screws and extended the
												tilting lever to gain access to the screw that connects the
												bracket base to the round panning servo horn. This time we want to
												remove the screw being careful not to move the bracket base
												undoing the calibration we just did. Lift the bracket base
												assembly off of the servo horn noting (and possibly marking) the
												orientation of the camera relative to the round servo horn (do not
												remove the horn, just the bracket assembly). Use the hot-glue gun
												to glue the bracket assembly to the round servo horn. Use the
												rubber ring and screw we removed to secure the base to the horn.
												Now, if we ever have to remove the bracket assembly we can unscrew
												the horn from the panning servo lifting it off of the junction
												box.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam18.jpg">
											<p>
												<b>Step 19:</b>
												If we leave the original screws in that hold the top camera
												mounting bracket they may tighten when tilting occurs. Also, the
												camera dome insert that resides just under the clear camera dome
												needs to turn with the camera as we pan so the opening is always
												centered with the camera lens. We accommodate both of these tasks
												by removing the two screws that hold the top bracket onto the
												bottom bracket base and run a long wire through both holes that
												extend out into the dome insert. When the camera pans the wire
												shaft will turn the dome insert while holding the top bracket onto
												the bracket base. You can use either 18 gauge wire or
												<a href="http://ugate.googlecode.com/files/cam-shaft-wire.jpg"
													rel="nofollow">a wire necklace from Michaels arts &amp; craft store</a>
												bent/cut to size. Obviously, the stronger the wire the better. The
												servos also came with a couple screw sleeves (shown above). We
												will use these to hold the wire shaft in place while its inside
												the dome insert.
											</p>
									</td>
									<td colspan="2">
										<iframe
											src="http://www.gmodules.com/gadgets/ifr?url=http%3A%2F%2Fgoogle-code-project-hosting-gadgets.googlecode.com%2Fsvn%2Fbuild%2Fprod%2Fvideo%2FgcVideo.xml&amp;up_video=http%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DnVsgEaDEsaw&amp;parent=%2F%2Fcode.google.com%2Fhosting&amp;container=code"
											width="308" frameborder="0" height="231">
										</iframe>
										<p>
											<b>Step 20:</b>
											First, remove the two screws that hold the top bracket to the
											bracket base. Cut a 2 and 3/4 inch wire section and insert it
											through the bracket base into the top bracket (where the screw used
											to be). Push it all the way through to the opposite side where the
											second screw used to be. You should now have a wire shaft sticking
											out both ends. Take one of the screw sleeves and hot-glue it to the
											inside wall of the dome insert about a 1/2 inch from the bottom of
											the dome insert so it lines up with the shaft when its setting on
											the dome base (there is very little clearance for pushing the
											sleeve through a hole from the outside- ignore those holes in the
											video). Slightly squeeze the dome insert from the front and back so
											the wire shaft can rest inside the sleeves we just glued into
											place. Twist the clear dome over the dome insert and onto the dome
											base.Rerun the code outlines in
											<b>Step 16</b>
											to ensure the insert is rotating as the camera is panning. If the
											camera lens is hitting the top of the dome when tilting up, add a
											few washers in-between the screw holes on the panning servo and the
											junction box. This should lower the camera assembly enough to avoid
											this if its an issue.
										</p>
									</td>
									<td>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h2>
							<a name="Microwave_Motion/Speed_Sensor_Assembly"></a>
							<u>Microwave Motion/Speed Sensor Assembly</u>
							<a href="#Microwave_Motion/Speed_Sensor_Assembly" class="section_anchor"></a>
						</h2>
						<p></p>
						<table title="Microwave Motion/Speed Sensor Assembly" cellpadding="3"
							cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/mw01.jpg">
											<p>
												<b>Step 1:</b>
												Due to the nature of X-Band microwave signals it can detect
												movement/speed through solid non-metallic objects such as walls
												(keep in mind that although it can detect motion from side to side
												it is more sensitive to motion moving toward/away from the device-
												similar to K-Band police radar). For this reason we can place the
												microwave sensor in the 4x4x4 junction box with minimal impact on
												the signal. The first thing we need to do is drill a hole in the
												center of the bottom of the 4x4x4 junction box using a 1/2 inch
												drill bit. This will be where the round portion of the servo just
												under the gear head will go (you may have to cut an additional
												notch on one side of the hole for it to fit). Once drilled, align
												the 4x4x4 box on the 6x6x4 box on the opposite end of the camera
												dome. Make sure the cover is on the 6x6x4 box when doing this to
												make sure the 4x4x4 box doesn't get in the way of it closing. Use
												a marker to mark the hole we just drilled through the 4x4x4 box
												onto the 6x6x4 box. This will be where the servo horn will
												protrude.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/mw02.jpg">
											<p>
												<b>Step 2:</b>
												Next, using one of the servos and a marker, trace around the servo
												while keeping the gear head centered on the mark drawn in
												<b>Step 1</b>
												. Make sure the servo body (opposite of the gear head) is pointing
												to the left of the 6x6x4 box (while looking at the 6x6x4 box
												opening). This will ensure that the orientation used in the
												program will be the same as the servo. Use a rotary tool with a
												cutting disk to cut out the traced edge being careful not to cut
												too far- we need the area to mount the servo screws. Mount the
												servo on the inside of the 6x6x4 box using the two silver screws
												provided with it (you can also use the two rubber washer provided
												if desired). Use a generous amount of outdoor silicone based caulk
												in between the two boxes to create a watertight seal. Once the
												servo is mounted and the caulk has dried, drill another 1/2 inch
												hole in the bottom, adjacent to the center hole drilled in
												<b>Step 1</b>
												. The hole should be orientated so the new hole is facing the back
												of the 4x4x4 box and should be drilled all the way through to the
												adjoining 6x6x4 box. This hole will allow the sensor wiring to be
												ran from the 4x4x4 box into the 6x6x4 box. Also, it might be a
												good idea to drill additional holes to affix some nuts/bolts to
												hold the 4x4x4 to the 6x6x4 box. This is optional, however, seeing
												that we are caulking the boxes together.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/mw03.jpg">
											<p>
												<b>Step 3:</b>
												Now that we have the junction boxes joined and the servo in place
												we can create the shield for the microwave sensor. You may have
												noticed that the circuit has 4 microstrip antennas built into it
												(opposite side from the sensitivity potentiometer). The 2 on the
												left are transmit and the 2 on the right are receive antennas. The
												device was designed with a broad beam pattern that will cover most
												of the areas surrounding the device in both the vertical and
												horizontal directions. In order to shield the signal so it will
												detect signals that are coming from the front of the device. The
												sausage can will work nicely for this as it meets the constraints
												of the junction box as well as the sensor. Keep in mind that while
												the sensor is within the can it will still detect motion to
												approximately 90 degrees from the opening. Also, because the can
												made of
												<b>thin</b>
												aluminum it will still detect motion from the sides and back of
												the can that are within a few feet. Consider that a tamper
												avoidance mechanism ;) Since the sensor has some exposed leads we
												don't want them to be shorted when we place it in the can. For
												this purpose line the interior of the can with several layers of
												electrical tape. Drill a hole for the square
												<font color="blue">blue</font>
												potentiometer to reside. Using a rotary tool and a cutting disk
												cut a slot on the side of the can near the bottom for the female
												jumper wires. Take the 2, 3/4 x 1/8 inch screws and screw them
												into the bottom of the can using the
												<u>third</u>
												indented ring as a guide (should line up with the mounting holes
												on the microwave sensor). Place the sensor into the can with the
												potentiometer facing the bottom of the can until the potentiometer
												is showing through the bottom of the can and the two screws are
												secured through the sensors mounting holes.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/mw04.jpg">
											<p>
												<b>Step 4:</b>
												Using some pliers to hold the base of the conduit hanger (opposite
												end of the nut/blot) bend the two hanger ends open so the shield
												can will fit through the two hanger ends. The pliers are used to
												ensure that the mounting hole on the base of the hanger doesn't
												bow while bending the hanger ends. Next, take a round servo horn
												(comes with servo) and a hot glue gun and glue it in place in the
												round mounting hole on the hanger base with the gear connection
												facing down (away from the hanger ends). After the glue has dried
												(usually around 5 minutes) place the hanger base down into the
												4x4x4 junction box and join the servo horn to the servo gear head.
												Gently rotate the servo/horn/hanger
												<b>clockwise</b>
												until the servo will not turn anymore. This should be your
												<b>zero</b>
												angle rotational position. If the hanger ends are not facing the
												front and back of the 6x6x4 box, lift the hanger off the servo and
												rotate it until it does. This orientation will be a
												<b>zero</b>
												degree rotational position where the can opening will be facing
												the left of the 6x6x4 box. As it rotates to
												<b>180</b>
												degrees the opening of the can will be facing the right of the
												box. Use the
												<b>black</b>
												screw that came with the servo to secure the horn to the servo.
												Place the can through the top of the bent hanger ends. Making sure
												that the can is centered between the hanger ends, squeeze the ends
												close together and secure the nut/bolt through the hole (this may
												cause a slight bend in the can as it's diameter is slightly over 2
												inches).
											</p>
									</td>
									<td>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h2>
							<a name="Sonar/PIR_Armature_Assembly"></a>
							<u>Sonar/PIR Armature Assembly</u>
							<a href="#Sonar/PIR_Armature_Assembly" class="section_anchor"></a>
						</h2>
						<p></p>
						<table title="Sonar/PIR Armature Assembly" cellpadding="3"
							cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar01.jpg">
											<p>
												<b>Step 1:</b>
												Next we need an weather tight compartment for our PIR and Sonar
												sensors. An outdoor single bulb floodlight will do the job. It's
												lightweight aluminum construction will prevent rust and keep UV
												rays at bay while being light enough for our micro servo to move
												it. We use a UV resistant PVC based rain poncho to protect our
												servos from the elements while providing the flexibility to pan
												and tilt the armature assembly.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar02.jpg">
											<p>
												<b>Step 2:</b>
												Remove the white PIR lens dome from the PIR sensor. The silver PIR
												sensing element should appear under the lens. We decided to remove
												the actual sensor element from the PIR PCB board so we can mount
												the PCB in a more readily accessible location inside the junction
												box. That way if we need to adjust the sensitivity and time pots
												(typically label on the PCB) we can do so without taking apart our
												PIR/Sonar armature. Using a de-soldering iron remove the silver
												PIR element by gently sucking the solder on the three element
												pins. Be careful not to touch the gray element window in the
												process. Next take three short male to male jumper wires and
												solder them into three holes where the element used to be mounted.
												It should look something like the illustration above. You may also
												need to trim the excess jumper pin so it will lay flat when we
												mount it in a later step (illustration does not show it trimmed
												yet).
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar03.jpg">
											<p>
												<b>Step 3:</b>
												Now we want to do a similar thing to the Sonar PCB that we did to
												the PIR PCB. There are two pins and should be labeled with a plus
												symbol and a minus symbol. We need to ensure we do not reverse
												these two pins when we reconnect them. For that reason you may
												notice that we used a permanent marker to label them (the positive
												pin is usually longer than the negative pin). The round black
												sensor element on the reverse side of the PCB is what sends and
												receives the ultrasonic signal we use to detect the distance an
												object is from our security device. Using a de-soldering iron,
												remove the black sonar element from the PCB. Take two short male
												to male jumper wires and solder them into the same holes where we
												removed the black sensor element from. You may also need to trim
												the excess jumper pin so it will lay flat when we mount it in a
												later step. It should like something like the illustration above.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar04.jpg">
											<p>
												<b>Step 4:</b>
												Next take the PIR element that we removed from
												<b>Step 2</b>
												and solder the three pins to a corner of a small PCB blank and
												three short male to male jumper wires. The jumper pins should be
												trimmed to mount obtain a flush mount.
												<b>NOTE: Illustration does not account for this</b>
												. Cut the corner section out of the PCB using a small hand saw or
												scissors. Make sure not to cut too close to the soldered holes
												because the PCB material has a tendency to crack when cut.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar05.jpg">
											<p>
												<b>Step 5:</b>
												Now we need to create a weatherproof cap that will fit in our
												floodlight holder. We used a piece of plexiglass, but any UV
												resistant plastic will work. It doesn't have to be transparent,
												but it will help when marking the holes to cut into it. Using a 1
												and 3/4 inch hole saw cut a round section of plexiglass. Place the
												PIR lens on top of the round plexiglass closest to either side to
												ensure there will be room for the sonar element. On the reverse
												side of the round plexiglass cutout mark the locations of the four
												PIR lens prongs. Using a 1/16 inch drill bit, drill all four prong
												holes that we just marked. Fasten the PIR lens into the four prong
												holes that were just drilled. Flip the plexiglass cutout over and
												mark the center where the PIR element will be placed (hint: there
												should be a small plastic mark in the center of the lens that you
												can use as a reference when marking the center point). Using a
												1/16 inch drill bit, drill the pilot hole for the PIR element-
												then use a 3/8 inch drill bit to drill the final hole where the
												element will be mounted. Next, place the black sonar element on
												the plexiglass cutout on the same side as the PIR lens. The lens
												and sonar element should span the diameter of the plexiglass
												cutout. On the reverse side mark the placement of the two sonar
												pins and drill two holes that are marked with a 1/16 inch drill
												bit. Holding the sonar element in place solder two short male to
												male jumper wires to the two pins on the sonar element (make sure
												the solder expands into the holes to make a secure solder. It
												should look something like the illustration above.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar06.jpg">
											<p>
												<b>Step 6:</b>
												Next, take the exterior grade caulk and secure the PIR element
												into the center hole drilled in
												<b>Step 5</b>
												. Make sure that the caulk does not get on the PIR element window.
												Let it dry for the allotted time designated on the caulk. It
												should look something like the illustration above.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar07.jpg">
											<p>
												<b>Step 7:</b>
												Now flip the plexiglass cutout over and caulk the black sonar
												element and PIR lens onto the plexiglass cutout making sure that
												there are no gaps and the caulk does not cover the sonar element
												head. It should look something like the illustration above.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar08.jpg">
											<p>
												<b>Step 8:</b>
												The floodlight holder is a little too long for our servos to
												handle so to make it a little more compact and to provide an area
												to mount the tilting servo we decided to cut it down to size using
												a rotary drill with a cutting attachment. The cast aluminum holder
												is thin and soft enough to easily make the cut. Using a
												screwdriver remove the ceramic bulb fixture. Also remove the
												rubber insert ring. Cut along the diameter of the floodlight
												holder using the rotary drill about 2 and a half inches from its
												opening- leaving about a 1 inch wide by 1 and a half inch tall
												section to mount the tilting servo horn to (see illustrations in
												later steps to get a visual on the cut). Once cut, use the
												exterior grade caulk to line the groove of the floodlight holder
												just inside the opening where the light bulb would normally rest
												(where the rubber insert ring was). Now place the plexiglass
												cutout into the floodlight holder with the PIR lens/sonar element
												facing out. Put another layer of caulk to secure the plexiglass
												cutout to the floodlight holder. Make sure there are no holes
												remaining that would allow moisture to penetrate the plexiglass
												cutout. It should look something like the illustration above.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar09.jpg">
											<p>
												<b>Step 9:</b>
												Next, take one of the micro servos and a single arm horn that came
												with it and mount it to the floodlight holder sidewall so that the
												servo is facing the inside of the holder. To do this we need to
												drill a 1/16 inch hole where the small black screw that came with
												the servo will go and another similar sized hole that will line up
												with the center hole on the servo horn. Use the black screw to
												secure the floodlight holder to the servo horn and into the servo
												itself. Use a silver screw that came with the servo to mount the
												center hole on the servo horn to the floodlight holder. It may be
												necessary to cut a portion of the silver screw if it interferes
												with the rotation of the servo. It should look similar to the
												illustration above.
												<b>IMPORTANT: Make sure the the servo horn arm is facing toward
													the top and the gear is in it's farthest position toward the top
													(similar to the orientation of the servo in step 13 of the Camera
													Dome Assembly).</b>
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar10.jpg">
											<p>
												<b>Step 10:</b>
												Now we need to attach the panning servo to the tilting servo. Use
												the black screw that came with the servo to attach the round horn.
												You may need to remove the sticker from the surface of the titling
												servo we mounted in
												<b>Step 9</b>
												. Use a glue gun the affix the round panning servo horn to the
												flat side of the tilting servo. we also used some exterior caulk
												around the round servo horn. It should look similar to the
												illustration above.
												<b>IMPORTANT: Make sure the the servo horn arm is facing toward
													the top and the gear is in it's farthest position toward the top
													(similar to the orientation of the servo in step 13 of the Camera
													Dome Assembly).</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar11.jpg">
											<p>
												<b>Step 11:</b>
												Remove the black and silver screw that secures the floodlight
												holder to the tilting servo horn to prevent it from getting in the
												way. We will remount it later. Place a 2 inch test knockout cap in
												the lower left outside corner of the junction box cover. Make sure
												it does not overlap the seal/screw track on the reverse side of
												the junction box cover. Using a rotary drill with a cutting disc
												attachment cutout a section of a 2 inch test knockout cap to mount
												the PIR/sonar panning servo from
												<b>Step 10</b>
												and repeat the cut into the cover itself ensuring both the
												knockout and cover cutout are aligned. Secure the panning servo to
												the knockout using two of the silver screws that came with the
												servo (you may have to use a 1/16 inch drill bit to drill a pilot
												hole before securing the screws). It should look similar to the
												illustration above.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar12.jpg">
											<p>
												<b>Step 12:</b>
												Using a 1/16 inch drill bit, drill two holes through the knockout
												and into the junction box cover. Using two of the servo mounting
												screws that we have left over from previous steps (or better yet,
												use some screws with smaller heads) mount the knockout onto the
												cover (you may have to use a 1/16 inch drill bit to drill a pilot
												hole before securing the screws). Now drill a 3/8 inch hole to the
												left of the panning servo into the knockout and through the cover.
												It should resemble the illustration above. Remove the two silver
												screws used to mount the knockout onto the cover.
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar13.jpg">
											<p>
												<b>Step 10:</b>
												Using an exterior grade caulk flip the knockout/servo over and
												caulk the rim of the knockout. Place the knockout/panning servo
												back onto the junction box cover. Line up the silver screws and
												remount the knockout onto the cover.
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar14.jpg">
											<p>
												<b>Step 11:</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/pirsonar15.jpg">
											<p>
												<b>Step 12:</b>
											</p>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h2>
							<a name="Circuit_Assembly_and_Wiring"></a>
							<u>Circuit Assembly and Wiring</u>
							<a href="#Circuit_Assembly_and_Wiring" class="section_anchor"></a>
						</h2>
						<p></p>
						<table title="Circuit Assembly and Wiring" cellpadding="3"
							cellspacing="3">
							<tbody>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 1:</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 2:</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 3:</b>
											</p>
									</td>
								</tr>
								<tr align="left" valign="top">
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 4:</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 5:</b>
											</p>
									</td>
									<td>
										<img src="http://ugate.googlecode.com/files/cam1.jpg">
											<p>
												<b>Step 6:</b>
											</p>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h1>
							<a name="Controlling_Peripherals"></a>
							<u>Controlling Peripherals</u>
							<a href="#Controlling_Peripherals" class="section_anchor"></a>
						</h1>
						<p>
							All of the hardware components work off of a single command interface
							that is driven by the universal remote
							<a href="http://code.google.com/p/ugate/#Command_Codes" rel="nofollow">Command
								Codes</a>
							. Commands sent from the GUI, email, or IR remote will all correspond
							to these codes. There are some command codes that are not available on
							the remote control that are available using the GUI or email (i.e. get
							the current sonar/PIR distance, access key codes, etc.).
						</p>
						<h2>
							<a name="The_Universal_Remote:"></a>
							<u>The Universal Remote:</u>
							<a href="#The_Universal_Remote:" class="section_anchor"></a>
						</h2>
						<p></p>
						<p> The Universal Remote is used for initiating predefined commands to
							operate peripherals that are attached to the Arduino. Some of the
							features include pan/tilt of the camera, pan/tilt of the sonar/PIR
							distance measurement device (controls the intruder sensing mechanism),
							taking pictures at different resolutions (i.e. VGA and QVGA), toggle
							opening/closing an entrance gate, etc. </p>
						<p></p>
						<h3>
							<a name="Programming_The_Universal_Remote"></a>
							<u>Programming The Universal Remote</u>
							<a href="#Programming_The_Universal_Remote" class="section_anchor"></a>
						</h3>
						<p></p>
						<table width="75%">
							<tbody>
								<tr>
									<td>
										<img src="http://ugate.googlecode.com/files/GE-24944.jpg">
									</td>
									<td valign="top">
										<p>
											There are many universal remotes to choose from with many types of
											protocols to operate the remote with (i.e.
											<a href="http://en.wikipedia.org/wiki/RC5" rel="nofollow">RC5</a>
											,
											<a href="http://en.wikipedia.org/wiki/RC6" rel="nofollow">RC6</a>
											,
											<a href="http://www.edcheung.com/automa/sircs.htm" rel="nofollow">Sony SIRCS
											</a>
											,
											<a href="http://en.wikipedia.org/wiki/Ruwido" rel="nofollow">Ruwido R-Step
											</a>
											, NEC TC101 etc.). Instead of trying to accommodate all of the
											protocols and considering the availability of programmable
											universal remotes it's easier just to stick with one. This project
											uses the
											<i>Sony SIRC</i>
											protocol (if you'd like to use a different protocol or would like
											to use multiple protocols
											<a
												href="http://www.arcfn.com/2009/08/multi-protocol-infrared-remote-library.html"
												rel="nofollow">this link</a>
											will get you started).
											<i>Sony SIRC</i>
											can be 12, 15, or 20 bit. 12 bit codes have been chosen to reduce
											the number of pulses that have to be decoded (NOTE: If your remote
											will not accept the code you should be able to choose any code that
											is 12 bit). Also, each universal remote code is setup to use or not
											use optional features on your remote control.
											<i>Sony SIRC</i>
											code for device
											<b>0380</b>
											for the
											<font color="blue">
												<b>TV</b>
											</font>
											and
											<b>0389</b>
											for the
											<font color="blue">
												<b>DVD</b>
											</font>
											player has been chosen for this project. The DVD is also programmed
											so that features like play, rewind, fast forward, record, stop,
											pause, etc. are transmitted and are available for decoding.
											<font color="red">
												<b>The TV should always be selected.</b>
											</font>
											. Most remotes will fall back on the programmed DVD device when
											features like play, rewind, fast forward, record, stop, pause, etc.
											are used. The
											<a href="http://www.google.com/search?q=GE+24944" rel="nofollow">GE
												Universal Remote (24944)</a>
											has been chosen as the remote control, but just about any universal
											remote should work fine.
										</p>
										<p>Many remotes can be programmed in a similar fashion to the steps
											below (if necessary see your remotes instructions for programming
											your specific remote): </p>
										<ol>
											<li>Press and hold the "Setup" button until the red LED stays on
											</li>
											<li>
												Press and release the
												<font color="blue">
													<b>TV</b>
												</font>
											</li>
											<li>
												Enter
												<i>Sony SIRC</i>
												code
												<b>0380</b>
												using the numeric keypad (The red LED should turn off- flashing
												indicates the code was not accepted)
											</li>
											<li>Next, press and hold the "Setup" button until the red LED stays
												on</li>
											<li>
												Press and release the
												<font color="blue">
													<b>DVD</b>
												</font>
											</li>
											<li>
												Enter
												<i>Sony SIRC</i>
												code
												<b>0389</b>
												using the numeric keypad (The red LED should turn off- flashing
												indicates the code was not accepted)
											</li>
										</ol>
										<p></p>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h3>
							<a name="Operating_The_Universal_Remote"></a>
							<u>Operating The Universal Remote</u>
							<a href="#Operating_The_Universal_Remote" class="section_anchor"></a>
						</h3>
						<p></p>
						<p>
							A security system wouldn't be very secure without an access code. So,
							by default to operate any of the features of the remote control a
							<b>3</b>
							-digit code must be entered before UGate functions can be accessed. On
							first upload of the ugate.pde to your Arduino the access code will
							default to
							<b>123</b>
							. When the
							<b>3</b>
							-digit code is entered in sequence it will unlock the UGate functions
							described in the table above. Successful access code entries are
							indicated by a blue or green flashing LED. Unsuccessful attempts will
							cause the red LED to blink. Access to the UGate functions will be
							available until the
							<b>Mute</b>
							button is pressed or the access code has timed out (default session
							timeout is
							<u>
								<b>30 seconds</b>
							</u>
							). Once the proper access code has been entered and accepted it can be
							changed by pressing the
							<b>Input</b>
							button on the remote and entering your new
							<b>3</b>
							-digit code. After access has been granted, pressing any one of the
							predefined UGate function commands will place that command into a
							buffered queue with each command getting executed in the order it was
							received. There are a few important key points to remember when using
							the universal remote:
							<b></b>
						</p>
						<ol>
							<b>
								<li>
									When a key is pressed the LED will briefly glow
									<font color="red">
										<b>red</b>
									</font>
									when
									<font color="red">rejected</font>
									,
									<font color="green">
										<b>green</b>
									</font>
									when
									<font color="green">accepted</font>
									,
									<font color="blue">
										<b>blue</b>
									</font>
									when
									<font color="blue">executing a command</font>
									, and
									<font color="gray">
										<b>white</b>
									</font>
									when entering
									<font color="gray">
										<b>access codes</b>
									</font>
									. (LED indicator for IR remote only)
								</li>
								<li>
									If
									<u>30 seconds</u>
									elapses in between any key input the session will timeout and
									subsequent commands cannot be executed until the access code has been
									reentered
								</li>
								<li>If the session times out during entry of a new access code it will
									revert back to the original access code</li>
								<li>
									When an attempt is made to change the access code via the universal
									remote it will be sent/stored on your host computer. If the host
									computer is offline at the time of the change the new access code
									will only be valid until the host comes back online (the host sends a
									restore
									<a href="http://code.google.com/p/ugate/#Command_Codes" rel="nofollow">command
									</a>
									to the Arduino that will cause the access code to be overwritten with
									the one stored on the host)
								</li>
								<li>
									If your Arduino looses power or is disconnected for any reason, a
									restore request
									<a href="http://code.google.com/p/ugate/#Command_Codes" rel="nofollow">command
									</a>
									is made to the host computer every
									<u>minute</u>
									until the Arduino receives a restore response
									<a href="http://code.google.com/p/ugate/#Command_Codes" rel="nofollow">command
									</a>
								</li>
								<li>
									The command queue is limited to
									<u>3</u>
									commands. Any commands entered beyond this threshold will cause the
									oldest command to be dropped.
								</li>
								<li>Due to the limitations of the Arduino's serial communications
									reliance upon timers... commands from the universal remote will be
									momentarily ignored during the wireless transfer of photos</li>
							</b>
						</ol>
						<p></p>
						<p></p>
						<h4>
							<a name="Command_Codes"></a>
							<u>Command Codes</u>
							<a href="#Command_Codes" class="section_anchor"></a>
						</h4>
						<p></p>
						<p>
							Command codes are used to control some of the features of the device
							when in close proximity. Most commands can be accessed from the
							<a href="http://code.google.com/p/ugate/#UGate_GUI" rel="nofollow">GUI</a>
							, but there are a limited number of commands that can be accessed from
							the universal remote. The following outlines the available commands.
							Values in
							<font color="blue">blue</font>
							denote
							<i>Sony SIRC</i>
							0380 (TV) and 0389 (DVD) decoded values.
							<font color="red">Red</font>
							values are GUI only commands. All values are maintained on the host
							computer within the configuration file. The reason the preferences are
							stored on the host computer is due to the limited capacity/capability
							of Arduino's
							<a
								href="http://www.chipkit.org/wiki/index.php?title=ChipKIT_Uno32#Using_simulated_EEPROM"
								rel="nofollow">EEPROM</a>
							(on-board memory). Each time the GUI application is started (or when
							manually updated in the
							<a href="http://code.google.com/p/ugate/#UGate_GUI" rel="nofollow">GUI</a>
							) the preferences will be transferred to the Arduino using the
							following commands.
						</p>
						<p></p>
						<p></p>
						<table class="tableborder">
							<tbody>
								<tr>
									<td valign="top">
										<table title="Command Mapping" border="1" cellpadding="3"
											cellspacing="3">
											<tbody>
												<tr align="center">
													<th>Command</th>
													<th>IR Remote Key</th>
													<th>IR Remote Function</th>
													<th>GUI Function</th>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">0</font>
													</td>
													<td>Numeric Keypad 1</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">1</font>
													</td>
													<td>Numeric Keypad 2</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">2</font>
													</td>
													<td>Numeric Keypad 3</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">3</font>
													</td>
													<td>Numeric Keypad 4</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">4</font>
													</td>
													<td>Numeric Keypad 5</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">5</font>
													</td>
													<td>Numeric Keypad 6</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">6</font>
													</td>
													<td>Numeric Keypad 7</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">7</font>
													</td>
													<td>Numeric Keypad 8</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">8</font>
													</td>
													<td>Numeric Keypad 9</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">9</font>
													</td>
													<td>Numeric Keypad 0</td>
													<td>Used in a 3-digit combination with other numbers for an
														access code</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">11</font>
													</td>
													<td>Enter</td>
													<td>Attempts to calibrate the Laser/IR Receiver sensor position
														with the reflective mirror</td>
													<td>Same as IR Remote</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">16</font>
													</td>
													<td>Cannel/Page Up</td>
													<td>
														Tilt the Camera or Sonar/PIR armature
														<i>up</i>
														by 10 degrees (Camera or Sonar/PIR armature selection determined
														by
														<b>Menu</b>
														)
													</td>
													<td>Same as IR Remote (with previous device selection and angle
														degree amount)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">17</font>
													</td>
													<td>Channel/Page Down</td>
													<td>
														Tilt the Camera or Sonar/PIR armature
														<i>down</i>
														by 10 degrees (Camera or Sonar/PIR armature selection determined
														by
														<b>Menu</b>
														)
													</td>
													<td>Same as IR Remote (with previous device selection and angle
														degree amount)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">18</font>
													</td>
													<td>Volume Up</td>
													<td>
														Pan the Camera or Sonar/PIR armature to the
														<i>right</i>
														by 10 degrees (Camera or Sonar/PIR armature selection determined
														by
														<b>Menu</b>
														)
													</td>
													<td>Same as IR Remote (with previous device selection and angle
														degree amount)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">19</font>
													</td>
													<td>Volume Down</td>
													<td>
														Pan the Camera or Sonar/PIR armature to the
														<i>left</i>
														by 10 degrees (Camera or Sonar/PIR selection determined by
														<b>Menu</b>
														)
													</td>
													<td>Same as IR Remote (with previous device selection and angle
														degree amount)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">20</font>
													</td>
													<td>Mute</td>
													<td>Logs out of universal remote session (no more commands can be
														executed after pressing this key until the access code is
														re-entered)</td>
													<td>Same as IR Remote</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">21</font>
													</td>
													<td>On/Off Power</td>
													<td>Turns on/off the alarm trip for all sensors</td>
													<td>Same as IR Remote</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">24</font>
													</td>
													<td>Stop</td>
													<td>TBD</td>
													<td>TBD</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">25</font>
													</td>
													<td>Pause</td>
													<td>TBD</td>
													<td>TBD</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">26</font>
													</td>
													<td>Play</td>
													<td>TBD</td>
													<td>TBD</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">27</font>
													</td>
													<td>Rewind</td>
													<td>TBD</td>
													<td>TBD</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">28</font>
													</td>
													<td>Fast Forward</td>
													<td>TBD</td>
													<td>TBD</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">29</font>
													</td>
													<td>Record</td>
													<td>Takes a picture at the predetermined resolution and emails it
														to the recipients defined via the GUI (if no recipients are
														defined it will only save the image to the computers hard drive-
														directory defined in the GUI tool)</td>
													<td>Same as IR Remote</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">37</font>
													</td>
													<td>Input</td>
													<td>Pressed before entering a new access code</td>
													<td>
														Same as IR Remote (same command will also contain the
														<b>3</b>
														digit access key to set in the same call)
													</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">58</font>
													</td>
													<td>Menu (Info)</td>
													<td>Toggles panning/tilt controls between the Camera (default)
														and the Sonar/PIR armature</td>
													<td>None</td>
												</tr>
												<tr align="center">
													<td>
														<font color="blue">59</font>
													</td>
													<td>Last/Previous Channel (an additional key denoted as a period
														may also be used on some remotes)</td>
													<td>Toggles opening/closing an entrance gate</td>
													<td>Same as IR Remote</td>
												</tr>
											</tbody>
										</table>
									</td>
									<td valign="top">
										<table title="Command Mapping" border="1" cellpadding="3"
											cellspacing="3">
											<tbody>
												<tr align="center">
													<th>Command</th>
													<th>IR Remote Key</th>
													<th>IR Remote Function</th>
													<th>UGate Function</th>
												</tr>
												<tr align="center">
													<td>
														<font color="red">100</font>
													</td>
													<td>None</td>
													<td>None</td>
													<td>Moves the Camera by a specified angle degree (same command
														will also contain a trailing tilt or panning command and an
														angle value)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="red">101</font>
													</td>
													<td>None</td>
													<td>None</td>
													<td>Moves the Sonar/PIR armature by a specified angle degree
														(same command will also contain a trailing tilt or panning
														command and an angle value)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="red">102</font>
													</td>
													<td>None</td>
													<td>None</td>
													<td>Moves the Microwave sensor by a specified angle degree (same
														command will also contain a trailing panning command and an
														angle value)</td>
												</tr>
												<tr align="center">
													<td>
														<font color="red">103</font>
													</td>
													<td>None</td>
													<td>None</td>
													<td>
														Sends the current sensor readings from the Arduino to the host
														computer. Index order:
														<p> 0. Initiating command </p>
														<ol>
															<li>Index of any readings that failed to be read (value will be
																zero if no failures exist)</li>
															<li>Current Sonar distance reading (feet)</li>
															<li>Current Sonar distance reading (inches)</li>
															<li>Current IR distance reading (feet)</li>
															<li>Current IR distance reading (inches)</li>
															<li>Current Microwave clocked speed (cycles/second)</li>
															<li>Current Gate state (0=closed, 1=open)</li>
														</ol>
														<p></p>
													</td>
												</tr>
												<tr align="center">
													<td>
														<font color="red">104</font>
													</td>
													<td>None</td>
													<td>None</td>
													<td>
														Sends all of the settings from the Arduino to the host computer
														(see
														<a href="http://code.google.com/p/ugate/#Settings_Indexes"
															rel="nofollow">Settings Indexes</a>
														for the order of the values)
													</td>
												</tr>
												<tr align="center">
													<td>
														<font color="red">105</font>
													</td>
													<td>None</td>
													<td>None</td>
													<td>
														Syncs all of the settings from the host computer to the Arduino
														(see
														<a href="http://code.google.com/p/ugate/#Settings_Indexes"
															rel="nofollow">Settings Indexes</a>
														for the order of the values)
													</td>
												</tr>
											</tbody>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
						<p></p>
						<h5>
							<a name="Settings_Indexes"></a>
							<u>Settings Indexes</u>
							<a href="#Settings_Indexes" class="section_anchor"></a>
						</h5>
						<p>
							<font size="1">
								<b>
									0. Initiating command (from the
									<a href="http://code.google.com/p/ugate/#Command_Codes" rel="nofollow">Command
										Codes</a>
									)
								</b>
							</font>
						</p>
						<ol>
							<font size="1">
								<b>
									<li>Index of any settings that failed to be read/set (value will be
										zero if no failures exist)</li>
									<li>Enable/Disable the use of universal remote access (0=off, 1=on)
									</li>
									<li>Access key code (1st entry)</li>
									<li>Access key code (2nd entry)</li>
									<li>Access key code (3rd entry)</li>
									<li>Gate open/close remote access- if applicable (0=off, 1=on)</li>
									<li>Sonar distance threshold before trip (feet)</li>
									<li>Sonar distance threshold before trip (inches)</li>
									<li>Sonar delay between alarm trips (minutes)</li>
									<li>PIR delay between alarm trips (minutes)</li>
									<li>Sonar/PIR pan angle (degrees)</li>
									<li>Sonar/PIR tilt angle (degrees)</li>
									<li>Microwave speed threshold before trip (cycles/second)</li>
									<li>Microwave delay between alarm trips (minutes)</li>
									<li>Microwave pan angle (degrees)</li>
									<li>Laser distance threshold before trip (feet)</li>
									<li>Laser distance threshold before trip (inches)</li>
									<li>Laser delay between alarm trips (minutes)</li>
									<li>Laser pan angle (degrees)</li>
									<li>Laser tilt angle (degrees)</li>
									<li>
										Multi-alarm trip state value (one of the following values):
										<br>
											<br>
												0 = Any sensor that is tripped will signal alarm
												<br>
													<br>
														1 = Only Laser trip will signal an alarm
														<br>
															<br>
																2 = Only Microwave trip will signal an alarm
																<br>
																	<br>
																		3 = Both Laser and Microwave have to be tripped in order to
																		signal alarm
																		<br>
																			<br>
																				4 = Only PIR trip will signal an alarm
																				<br>
																					<br>
																						5 = Both Laser and PIR have to be tripped in order to
																						signal alarm
																						<br>
																							<br>
																								6 = Both Microwave and PIR have to be tripped in order
																								to signal alarm
																								<br>
																									<br>
																										7 = Laser, Microwave, and PIR all have to be tripped
																										in order to signal alarm
																										<br>
																											<br>
																												8 = Only Sonar trip will signal an alarm
																												<br>
																													<br>
																														9 = Both Laser and Sonar have to be tripped in
																														order to signal alarm
																														<br>
																															<br>
																																10 = Both Microwave and Sonar have to be
																																tripped in order to signal alarm
																																<br>
																																	<br>
																																		11 = Laser, Microwave, and Sonar all have to
																																		be tripped in order to signal alarm
																																		<br>
																																			<br>
																																				12 = Both PIR and Sonar have to be tripped
																																				in order to signal alarm
																																				<br>
																																					<br>
																																						13 = Laser, PIR, and Sonar all have to
																																						be tripped in order to signal alarm
																																						<br>
																																							<br>
																																								14 = Microwave, PIR, and Sonar all
																																								have to be tripped in order to signal
																																								alarm
																																								<br>
																																									<br>
																																										15 = Laser, Microwave, PIR, and
																																										Sonar all have to be tripped in
																																										order to signal alarm
																																										<br>
																																											<br>
									</li>
									<li>Camera image resolution used when taking pictures (0=QVGA, 1=VGA)
									</li>
									<li>Camera pan angle (degrees)</li>
									<li>Camera tilt angle (degrees)</li>
									<li>
										Camera
										<u>priority</u>
										for the
										<u>Sonar</u>
										alarm when multiple alarms are tripped simultaneously (1-4 For
										example, if sensor
										<b>A</b>
										has a priority of
										<b>1</b>
										and sensor
										<b>B</b>
										has a priority of
										<b>2</b>
										and both alarms are triggered, the camera pan/tilt angle for sensor
										<b>A</b>
										will be used to position the camera before an picture is taken.
										After the picture is taken the camera will return to the previous
										pan/tilt position)
									</li>
									<li>
										Camera
										<u>pan</u>
										angle when the
										<u>Sonar</u>
										alarm is tripped (181=maintain camera pan position, 0-180=pan angle)
									</li>
									<li>
										Camera
										<u>tilt</u>
										angle when the
										<u>Sonar</u>
										alarm is tripped (181=maintain camera tilt position, 0-180=tilt
										angle)
									</li>
									<li>
										Camera
										<u>priority</u>
										for the
										<u>PIR</u>
										alarm when multiple alarms are tripped simultaneously (1-4 For
										example, if sensor
										<b>A</b>
										has a priority of
										<b>1</b>
										and sensor
										<b>B</b>
										has a priority of
										<b>2</b>
										and both alarms are triggered, the camera pan/tilt angle for sensor
										<b>A</b>
										will be used to position the camera before an picture is taken.
										After the picture is taken the camera will return to the previous
										pan/tilt position)
									</li>
									<li>
										Camera
										<u>pan</u>
										angle when the
										<u>PIR</u>
										alarm is tripped (181=maintain camera pan position, 0-180=pan angle)
									</li>
									<li>
										Camera
										<u>tilt</u>
										angle when the
										<u>PIR</u>
										alarm is tripped (181=maintain camera tilt position, 0-180=tilt
										angle)
									</li>
									<li>
										Camera
										<u>priority</u>
										for the
										<u>Microwave</u>
										alarm when multiple alarms are tripped simultaneously (1-4 For
										example, if sensor
										<b>A</b>
										has a priority of
										<b>1</b>
										and sensor
										<b>B</b>
										has a priority of
										<b>2</b>
										and both alarms are triggered, the camera pan/tilt angle for sensor
										<b>A</b>
										will be used to position the camera before an picture is taken.
										After the picture is taken the camera will return to the previous
										pan/tilt position)
									</li>
									<li>
										Camera
										<u>pan</u>
										angle when the
										<u>Microwave</u>
										alarm is tripped (181=maintain camera pan position, 0-180=pan angle)
									</li>
									<li>
										Camera
										<u>tilt</u>
										angle when the
										<u>Microwave</u>
										alarm is tripped (181=maintain camera tilt position, 0-180=tilt
										angle)
									</li>
									<li>
										Camera
										<u>priority</u>
										for the
										<u>Laser</u>
										alarm when multiple alarms are tripped simultaneously (1-4 For
										example, if sensor
										<b>A</b>
										has a priority of
										<b>1</b>
										and sensor
										<b>B</b>
										has a priority of
										<b>2</b>
										and both alarms are triggered, the camera pan/tilt angle for sensor
										<b>A</b>
										will be used to position the camera before an picture is taken.
										After the picture is taken the camera will return to the previous
										pan/tilt position)
									</li>
									<li>
										Camera
										<u>pan</u>
										angle when the
										<u>Laser</u>
										alarm is tripped (181=maintain camera pan position, 0-180=pan angle)
									</li>
									<li>
										Camera
										<u>tilt</u>
										angle when the
										<u>Laser</u>
										alarm is tripped (181=maintain camera tilt position, 0-180=tilt
										angle)
									</li>
								</b>
							</font>
						</ol>
						<font size="1">
							<b>
							</b>
						</font>
						<p></p>
						<h4>
							<a name="Controlling_The_Camera_Position"></a>
							<u>Controlling The Camera Position</u>
							<a href="#Controlling_The_Camera_Position" class="section_anchor"></a>
						</h4>
						<p></p>
						<p>
							Once a valid access code has been entered into the remote the on-board
							camera can be panned (x-axis/azimuth) or tilted (y-axis/elevation) to
							establish a desired viewing angle. The
							<b>Menu</b>
							button toggles the pan/tilt control between the camera (primary) and
							the sonar/PIR armature (secondary). Due to most remotes orientation
							relative to these buttons the
							<b>Channel/Page Up</b>
							button is used tilt the camera
							<i>up</i>
							and the
							<b>Channel/Page Down</b>
							button is used to tilt the the camera
							<i>down</i>
							. Similarly, the
							<b>Volume Down</b>
							button pans the camera to the
							<i>left</i>
							and the
							<b>Volume Up</b>
							button pans the camera to the
							<i>right</i>
							.
						</p>
						<p></p>
						<h4>
							<a name="Controlling_The_Sonar/PIR_Armature"></a>
							<u>Controlling The Sonar/PIR Armature</u>
							<a href="#Controlling_The_Sonar/PIR_Armature" class="section_anchor"></a>
						</h4>
						<p></p>
						<p>
							Once a valid access code has been entered into the remote the sonar/PIR
							armature can be panned (x-axis/azimuth) or tilted (y-axis/elevation) to
							establish a desired presence/distance detection angle. The
							<b>Menu</b>
							button toggles the pan/tilt control between the camera (primary) and
							the sonar/PIR armature (secondary). So, you will have to click the
							<b>Menu</b>
							button before pressing the following buttons or else you will be moving
							the camera- not the sonar/PIR armatures. Due to most remotes
							orientation relative to these buttons the
							<b>Channel/Page Up</b>
							button is used to tilt the sonar/PIR armature
							<i>up</i>
							and the
							<b>Channel/Page Down</b>
							button is used to tilt the sonar/PIR armature
							<i>down</i>
							. Similarly, the
							<b>Volume Down</b>
							button pans the sonar/PIR armature to the
							<i>left</i>
							and the
							<b>Volume Up</b>
							button pans the sonar/PIR armature to the
							<i>right</i>
							.
						</p>
						<p></p>
						<h4>
							<a name="Controlling_The_Microwave_Position"></a>
							<u>Controlling The Microwave Position</u>
							<a href="#Controlling_The_Microwave_Position" class="section_anchor"></a>
						</h4>
						<p></p>
						<p>
							The microwave sensor has a very broad beam pattern that will span most
							of the vertical space in the direction it is pointing to. So, the only
							logical movement would be to limit it to panning. Even so, the device
							is housed in a junction box and is not visible. Therefore, there is no
							command that will pan the microwave sensor via the universal remote.
							However, the
							<a href="http://code.google.com/p/ugate/#UGate_GUI" rel="nofollow">GUI
								application</a>
							has a visual aid to indicate the direction the sensor is pointing to
							and can be adjusted accordingly.
						</p>
						<p></p>
						<h4>
							<a name="Controlling_The_Laser/IR_Receiver_(Tripwire)_Position"></a>
							<u>Controlling The Laser/IR Receiver (Tripwire) Position</u>
							<a href="#Controlling_The_Laser/IR_Receiver_%28Tripwire%29_Position"
								class="section_anchor"></a>
						</h4>
						<p></p>
						<p>
							The Laser/IR Receiver position needs to be precise and should not be
							moved manually. Instead, the Laser can be calibrated by using the
							<b>Enter</b>
							button on your universal remote. The Laser/IR Receiver will scan all
							available pan/tilt angles until it finds the return signal from the
							pulsed laser or until it exhausts all available pan/tilt angles. Prior
							to calibration the reflective mirror should be aligned in the general
							direction of the Laser/IR Receiver.
						</p>
						<p></p>
						<h4>
							<a name="Controlling_The_Entrance_Gate"></a>
							<u>Controlling The Entrance Gate</u>
							<a href="#Controlling_The_Entrance_Gate" class="section_anchor"></a>
						</h4>
						<p></p>
						<p>
							Once a valid access code has been entered into the remote the gate can
							be toggled (open/closed) by pressing the
							<b>Last (Previous Channel)</b>
							button. When pressed the last state of the gate is reversed. For
							instance, if the gate is shut it will open. If the gate is opened it
							will shut.
						</p>
						<p></p>
						<h2>
							<a name="UGate_GUI"></a>
							<u>UGate GUI</u>
							<a href="#UGate_GUI" class="section_anchor"></a>
						</h2>
						<h2>
							<a name="Web_Server"></a>
							<u>Embedded Web Server</u>
							<a href="#Web_Server" class="section_anchor"></a>
						</h2>
						<h2>
							<a name="Email"></a>
							<u>Email</u>
							<a href="#Email" class="section_anchor"></a>
						</h2>
						<h2>
							<a name="iPhone_Setup"></a>
							<u>iPhone Setup</u>
							<a href="#iPhone_Setup" class="section_anchor"></a>
						</h2>
						<h2>
							<a name="Android_Setup"></a>
							<u>Android Setup</u>
							<a href="#Android_Setup" class="section_anchor"></a>
						</h2>
</div>
