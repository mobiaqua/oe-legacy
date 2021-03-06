SUMMARY = "Userspace interface to the kernel DRM services"
DESCRIPTION = "The runtime library for accessing the kernel DRM services.  DRM \
stands for \"Direct Rendering Manager\", which is the kernel portion of the \
\"Direct Rendering Infrastructure\" (DRI).  DRI is required for many hardware \
accelerated OpenGL drivers."
HOMEPAGE = "http://dri.freedesktop.org"
SECTION = "x11/base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://xf86drm.c;beginline=9;endline=32;md5=c8a3b961af7667c530816761e949dc71"
PROVIDES = "drm"
DEPENDS = "libpthread-stubs udev"

SRC_URI = "http://dri.freedesktop.org/libdrm/${BP}.tar.bz2 \
           file://remove-xorg-dep.patch \
           file://add-deps.patch \
          "

FILESPATHPKG =. "libdrm-${PN}:"

SRC_URI[md5sum] = "f47bc87e28198ba527e6b44ffdd62f65"
SRC_URI[sha256sum] = "c77cc828186c9ceec3e56ae202b43ee99eb932b4a87255038a80e8a1060d0a5d"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-cairo-tests \
                 --without-cunit \
                 --enable-omap-experimental-api \
                 --disable-valgrind \
                 --disable-manpages \
                 --disable-kms \
                 --disable-freedreno \
                 --disable-intel \
                 --disable-radeon \
                 --disable-amdgpu \
                 --disable-nouveau \
                 --disable-vmwgfx \
                 --disable-vc4 \
                "
ALLOW_EMPTY_${PN}-drivers = "1"
PACKAGES =+ "${PN}-drivers ${PN}-omap"

RRECOMMENDS_${PN}-drivers = "${PN}-omap"

FILES_${PN}-omap = "${libdir}/libdrm_omap.so.*"

DEBUG_BUILD = "${@['no','yes'][bb.data.getVar('BUILD_DEBUG', d, 1) == '1']}"
CFLAGS_append = "${@['',' -O0 -g3'][bb.data.getVar('BUILD_DEBUG', d, 1) == '1']}"

do_rm_work() {
        if [ "${DEBUG_BUILD}" == "no" ]; then
                cd ${WORKDIR}
                for dir in *
                do
                        if [ `basename ${dir}` = "temp" ]; then
                                echo "Not removing temp"
                        else
                                echo "Removing $dir" ; rm -rf $dir
                        fi
                done
        fi
}
