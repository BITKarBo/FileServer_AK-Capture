File server for AK-CAPTURE

https://github.com/BITKarBo/AK-Capture
https://github.com/BITKarBo/FileServer_AK-Capture

Usage:

	Make own remote file server with FileServerini.jar by running it in your own server.
	
	Change --port parameter to mach your own port.
	Change --path FilePath to mach your own FilePath where you want save incoming gifs.
	There is also password that need to match with AK-CAPTURE client.
	
	Using from cmd args [PRIORITY #2]
		
		--port [port]
		--pass [password] <-- SHA256 Encryption
		--path [file path]<-- change your own
		
	Using from cfg.config [PRIORITY #1]
		
		--port [port]
		--pass [password] <-- SHA256 Encryption
		--path [/temp]  <-- change your own
Enjoy - KarBo_
