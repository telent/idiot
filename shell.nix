with import <nixpkgs> {};

stdenv.mkDerivation rec {
  name  = "idiot";
  version = "0.1.0";
  src = ./.;
  nativeBuildInputs = [ pkgs.leiningen pkgs.mosquitto ];
  leinBin = "${pkgs.leiningen}/bin/lein";
  shellHook = ''
    TMPDIR=/tmp emacsclient -e "(setq cider-lein-command \"${leinBin}\")"
  '';
}
