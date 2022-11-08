

function isEmpty( str ){
 return ( str == "" );
}

function isNumber( str ){
 return /^\d*(?:$|\.\d*$)/.test( str );
}

function isCharOrNum( str ){
 return /^\w+$/.test( str );
}

function isEmail( str ){
 return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test( str );
}

function isMobile( str ){
 return /^\d{11}$/.test( str );
}

function isPhone( str ){
 return /^\d{8}$/.test( str );
}

function isPassword( password ){
return /^[\w]{5,20}$/.test( password );
}

function isMessagemaxlength( str , maxlength )
{
 var j=0
 for (i=0;i<str.length;i++){
	if(str.charCodeAt(i)>255){
        j=j+2;
	}else{
        j++;
	}
 }
 if(j>maxlength){
   return false;
  }
 return true;
}

function isMessageminlength( str , minlength ){
var j=0
 for (i=0;i<str.length;i++){
   if(str.charCodeAt(i)>255){
        j=j+2;
	}
   j++;
 }
 if(j<minlength){
   return false;
  }
 return true
}

function IsSubString (str,sR){
var sTmp;
if(str.length==0){ return false;}
for (var i=0; i < str.length; i++){
sTmp= str.substring (i, i+1);
if (sR.indexOf (sTmp, 0)==-1) {return false;}
}
return true;
}
