INSTALL_PKGS += "\
                 alsa-utils-amixer alsa-utils-speakertest alsa-utils-alsactl alsa-utils-aplay \
                 e2fsprogs e2fsprogs-mke2fs dosfstools usbutils procps mc mtd-utils \
                 gdbserver gdb valgrind glibc-dbg tcf-agent openssh-sftp-server bluez-hcidump \
"

DEPENDS += "gdb-cross"
RDEPENDS += ""
RRECOMMENDS += ""

