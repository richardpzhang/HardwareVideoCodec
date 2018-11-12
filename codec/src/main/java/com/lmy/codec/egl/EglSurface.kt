/*
 * Copyright (c) 2018-present, lmyooyo@gmail.com.
 *
 * This source code is licensed under the GPL license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.lmy.codec.egl

import android.opengl.EGLContext
import android.opengl.GLES20
import com.lmy.codec.egl.entity.Egl
import com.lmy.codec.entity.CodecContext
import com.lmy.codec.helper.GLHelper
import com.lmy.codec.texture.impl.BaseTexture

/**
 * Created by lmyooyo@gmail.com on 2018/3/26.
 */
abstract class EglSurface<T> {
    internal var surface: T? = null
    var texture: BaseTexture? = null
    open var textureId: IntArray? = null
    internal var egl: Egl? = null

    abstract fun draw(transformMatrix: FloatArray?)

    fun clear() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        egl?.swapBuffers()
    }

    fun createTexture(target: Int) {
        if (null == textureId) {
            textureId = IntArray(1)
        } else {
            GLES20.glDeleteTextures(1, textureId, 0)
        }
        textureId!![0] = 0
        GLES20.glGenTextures(1, textureId, 0)
        GLES20.glBindTexture(target, textureId!![0])
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
        GLHelper.checkGLES2Error("createTexture")
    }

    open fun release() {
        egl?.makeCurrent()
        texture?.release()
        texture = null
        egl?.release()
        egl = null
//        surfaceTexture?.release()
//        surfaceTexture = null
//        if (null != textureId)
//            GLES20.glDeleteTextures(1, textureId, 0)
    }

    //更新xyst坐标
    abstract fun updateLocation(context: CodecContext)

    fun updateInputTexture(textureId: IntArray) {
        this.textureId = textureId
        texture?.textureId = textureId
    }

    fun makeCurrent() {
        egl?.makeCurrent()
    }

    fun getEglContext(): EGLContext? {
        return egl?.eglContext
    }
}