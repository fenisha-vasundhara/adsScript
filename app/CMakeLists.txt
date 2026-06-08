cmake_minimum_required(VERSION 3.22.1)

project(MessagerNative LANGUAGES C CXX)

if (CMAKE_SYSTEM_NAME STREQUAL "Android")
    # Ensure PT_LOAD segments use 16 KB alignment on Android (lld supports these flags).
    add_link_options(
        "-Wl,-z,max-page-size=0x4000"
        "-Wl,-z,common-page-size=0x4000"
    )
endif()
