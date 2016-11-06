package com.romhackhispano.ktacme

import java.io.File

operator fun File.get(name: String) = File(this, name)