DEPENDS = "libdce libdrm ffmpeg libgbm virtual/egl"

inherit autotools

PV = "1.0.6"
PR = "r0"
PR_append = "+gitr-${SRCREV}"

SRCREV = "c287ddf9a8346ff5d18df7ab70cfd7bc438b62e5"
SRC_URI = "git://git.ti.com/glsdk/omapdrmtest.git;protocol=git \
           file://0004-display-kmscube-align-width-on-128-bytes-to-please-Ducat.patch \
           file://0005-Hack-disp-kmscube-reduce-u-v-by-10.patch \
           file://disable-v4l-vpe.patch \
           file://revert-to-old-dce.patch \
           file://display-kmscube_revert.patch \
           file://viddec3test_revert.patch \
"

S = "${WORKDIR}/git"

FLAGS="-lm -lavcodec"

EXTRA_OECONF = "LDFLAGS='${FLAGS}' --disable-x11"