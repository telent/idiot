with import <nixpkgs> {};

stdenv.mkDerivation rec {
  name  = "idiot";
  version = "0.1.0";
  src = ./.;
  nativeBuildInputs = [ pkgs.leiningen ];
}
