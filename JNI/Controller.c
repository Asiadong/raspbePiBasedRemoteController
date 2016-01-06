#include "wiringPi.h"
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>

//#include "wiringPi.h"
#include "heaserver_MyServer_CloseThread.h"
#include "heaserver_MyServer.h"
#include "heaserver_MyServer_SocketThread.h"
JNIEXPORT jboolean JNICALL Java_heaserver_MyServer_control
  (JNIEnv *env, jobject obj, jbyteArray jarry){
	int timeout=300;
	int times=0;
	int i=0;
	int fd ;
	if ((fd = serialOpen ("/dev/ttyAMA0", 9600)) < 0)
  	{
    	//fprintf (stderr, "Unable to open serial device: %s\n", strerror (errno)) ;
    	return 0 ;
  	}
	if (wiringPiSetup () == -1)
  	{
    	//fprintf (stdout, "Unable to start wiringPi: %s\n", strerror (errno)) ;
    	return 0 ;
  	}
	unsigned char* data=(unsigned char*)((*env)->GetByteArrayElements(env,jarry, 0));
	jint length=(*env)->GetArrayLength(env,jarry);
	times=(int)length;
	for(;i<times;i++){
	serialPutchar(fd, data[i]);
	fflush (stdout) ;
	}//Of for i
    	while (timeout--)
    	{
      	if(serialDataAvail(fd)&&serialGetchar (fd)==0x55){
      	fflush (stdout) ;
	return 1;
	}//Of if
    	}//Of while
	return 0;
}//Of control
