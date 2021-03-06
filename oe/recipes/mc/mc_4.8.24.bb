require mc.inc
PR = "${INC_PR}.0"
HOMEPAGE = "http://www.midnight-commander.org/"

SRC_URI = "http://www.midnight-commander.org/downloads/${BPN}-${PV}.tar.bz2 \
           file://0001-mc-replace-perl-w-with-use-warnings.patch \
           file://nomandate.patch \
           file://0001-Ticket-4070-misc-Makefile.am-install-mc.lib-only-onc.patch \
           file://fixed_ncurses.patch \
           "
SRC_URI[md5sum] = "2621de1fa9058a9c41a4248becc969f9"
SRC_URI[sha256sum] = "cfcc4d0546d0c3a88645a8bf71612ed36647ea3264d973b1f28183a0c84bae34"

EXTRA_OECONF = "--without-x --without-samba \
--without-nfs --without-gpm-mouse \
--with-screen=ncurses \
ac_cv_path_PERL='/usr/bin/env perl' \
ac_cv_path_PYTHON='/usr/bin/env python' \
ac_cv_path_ZIP=${bindir}/zip \
ac_cv_path_UNZIP=${bindir}/unzip \
"

do_unpack_append() {
        bb.build.exec_func('do_utf8_conversion', d)
}
do_utf8_conversion() {
	cd ${S}/doc/hints
	iconv -f iso8859-1 -t utf-8 -o mc.hint.tmp mc.hint && mv mc.hint.tmp mc.hint
	iconv -f iso8859-1 -t utf-8 -o mc.hint.es.tmp mc.hint.es && mv mc.hint.es.tmp mc.hint.es
	iconv -f iso8859-1 -t utf-8 -o mc.hint.it.tmp mc.hint.it && mv mc.hint.it.tmp mc.hint.it
	iconv -f iso8859-1 -t utf-8 -o mc.hint.nl.tmp mc.hint.nl && mv mc.hint.nl.tmp mc.hint.nl
	iconv -f iso8859-2 -t utf-8 -o mc.hint.cs.tmp mc.hint.cs && mv mc.hint.cs.tmp mc.hint.cs
	iconv -f iso8859-2 -t utf-8 -o mc.hint.hu.tmp mc.hint.hu && mv mc.hint.hu.tmp mc.hint.hu
	iconv -f iso8859-2 -t utf-8 -o mc.hint.pl.tmp mc.hint.pl && mv mc.hint.pl.tmp mc.hint.pl
	iconv -f iso8859-5 -t utf-8 -o mc.hint.sr.tmp mc.hint.sr && mv mc.hint.sr.tmp mc.hint.sr
	iconv -f koi8-r -t utf8 -o mc.hint.ru.tmp mc.hint.ru && mv mc.hint.ru.tmp mc.hint.ru
	iconv -f koi8-u -t utf8 -o mc.hint.uk.tmp mc.hint.uk && mv mc.hint.uk.tmp mc.hint.uk
	iconv -f big5 -t utf8 -o mc.hint.zh.tmp mc.hint.zh && mv mc.hint.zh.tmp mc.hint.zh
	iconv -f iso8859-5 -t utf-8 -o mc.menu.sr.tmp mc.menu.sr && mv mc.menu.sr.tmp mc.menu.sr
	# convert docs to utf-8
	cd ${S}/doc/man/es
	iconv -f iso8859-1 -t utf-8 -o mc.1.in.tmp mc.1.in && mv mc.1.in.tmp mc.1.in
	iconv -f iso8859-1 -t utf-8 -o xnc.hlp.tmp xnc.hlp && mv xnc.hlp.tmp xnc.hlp
	cd ${S}/doc/man/hu
	iconv -f iso8859-2 -t utf-8 -o mc.1.in.tmp mc.1.in && mv mc.1.in.tmp mc.1.in
	iconv -f iso8859-2 -t utf-8 -o xnc.hlp.tmp xnc.hlp && mv xnc.hlp.tmp xnc.hlp
	cd ${S}/doc/man/it
	iconv -f iso8859-1 -t utf-8 -o mc.1.in.tmp mc.1.in && mv mc.1.in.tmp mc.1.in
	iconv -f iso8859-1 -t utf-8 -o xnc.hlp.tmp xnc.hlp && mv xnc.hlp.tmp xnc.hlp
	cd ${S}/doc/man/pl
	iconv -f iso8859-2 -t utf-8 -o mc.1.in.tmp mc.1.in && mv mc.1.in.tmp mc.1.in
	iconv -f iso8859-2 -t utf-8 -o xnc.hlp.tmp xnc.hlp && mv xnc.hlp.tmp xnc.hlp
	cd ${S}/doc/man/ru
	iconv -f koi8-r -t utf-8 -o mc.1.in.tmp mc.1.in && mv mc.1.in.tmp mc.1.in
	iconv -f koi8-r -t utf-8 -o xnc.hlp.tmp xnc.hlp && mv xnc.hlp.tmp xnc.hlp
	cd ${S}/doc/man/sr
	iconv -f iso8859-5 -t utf-8 -o mc.1.in.tmp mc.1.in && mv mc.1.in.tmp mc.1.in
	iconv -f iso8859-5 -t utf-8 -o xnc.hlp.tmp xnc.hlp && mv xnc.hlp.tmp xnc.hlp
	iconv -f iso8859-5 -t utf-8 -o mcserv.8.in.tmp mcserv.8.in && mv mcserv.8.in.tmp mcserv.8.in
	cd ${S}
}

do_configure_prepend() {

AUTOFOO="config.guess config.sub depcomp install-sh missing"

         for i in ${AUTOFOO}; do
           rm config/${i}
         done
}
