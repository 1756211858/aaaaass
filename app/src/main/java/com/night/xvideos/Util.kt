package com.night.xvideos

import android.content.Context
import android.widget.Toast

fun Any.ShortShow(context: Context, content: String) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}

//#hlsplayer > div.buttons-bar.right > img:nth-child(4)
fun Any.LongShow(context: Context, content: String) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()

}

fun Any.getJs(): String {
    //#hlsplayer > div.buttons-bar.right > img:nth-child(4)
    return "javascript:document.getElementsByClassName('buttons-bar.right')[4].addEventListener('click',function(){onClick.fullscreen();return false;});"
}

