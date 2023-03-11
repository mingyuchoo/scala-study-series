{
  description = "A basic devShell using flake-utils for Scala";

  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = nixpkgs.legacyPackages.${system};
      in
        rec {
          devShell = pkgs.mkShell {
            buildInputs = with pkgs; [
              coursier
              direnv
              jdk17
              gradle
              sbt
              scala_3
            ];
            shellHook = ''
              export LANG=C.UTF-8
              export EDITOR=emacs
              eval "$(direnv hook bash)"
              echo "Welcome to nix flake for Scala"
            '';
          };
        }
    );
}
