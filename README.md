JobGlRec
==================================

Configuration
---------------
```sh
#SFTP server
sftp.host=192.168.99.100
sftp.port=2222

sftp.user=foo

sftp.upload.path=/pmhftp/PMH/Report/
sftp.download.path=/pmhftp/TempCFR/Outbound/
sftp.ssh.keyfile=nopp
sftp.ssh.passphrase=

#File
file.name.prefix=GLRec_
file.name.dateformat=ddMMyyyy
file.share.path=payhshare/Finace/ONS_LOGS/BJMS/011/H001/GLREC
file.type.zip=xls

#Log4j
log.config.file=log4j.properties

#MAIL SERVER
mail.transport.protocol=smtp
mail.smtp.auth=true
mail.smtp.starttls.enable=true
mail.smtp.host=smtp.live.com
mail.smtp.port=25

#debug
mail.debug=true

#auth
mail.user=
mail.pass=

#content
mail.sender=
mail.to=
mail.cc=
mail.bcc=
mail.dateformat=dd-MM-yyyy
mail.subject=[FILE GLREC] - as of #dd-MM-yyyy from PAYMENTHUB
mail.message.html.file=body_message.html
```

Usage command
---------------
```sh
java -Dconfig.file=${config.properties} -jar ${PaymentHub.jar} ${mode}
```
  Use -Dconfig.file=${config.properties} to get your config
	
  Use -jar ${CFR.jar} to get your jarfile to run


