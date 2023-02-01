File server for AK-CAPTURE

Usage:

	Make own remote file server with FileServerini.jar by running it in your own server.
	
	Change --port parameter to mach your own port. Change --path FilePath to mach your own FilePath where you want save incoming gifs. There is also password that need to mach with AK-CAPTURE client.
	
	Using from args.
		
		--port [port]
		--pass [password] <-- SHA256 Encryption
		--path [file path]<-- change your own
		
	Using from cfg.config
		
		--port [port]
		--pass [password] <-- SHA256 Encryption
		--path [/temp]  <-- change your own