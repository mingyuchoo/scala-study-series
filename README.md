# scala-study-series

## Install Scala on your computer

### Linux

```bash
curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
cs setup
cs launch scala:3.7.3
cs launch scalac:3.7.3
```

## Using the Scala CLI

### Linux

```bash
curl -sSLf https://scala-cli.virtuslab.org/get | sh
```

## References

- https://docs.scala-lang.org/getting-started/install-scala.html
- https://scala-cli.virtuslab.org/install/
