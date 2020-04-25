<h1 align="center">
  <br>
    <img src="https://raw.github.com/TeamWizardry/LibrarianLib/1.15/logo/logo_500x500.png" title="LibrarianLib logo by 
    Tris Astral" width="200" height="200" alt="LibrarianLib Logo by Tris Astral">
  <br>
  LibrarianLib
  <br>
</h1>

## Using LibrarianLib

Due to the fact that (as of April 24th 2020) ForgeGradle's `fg.deobf` is broken (it doesn't properly link sources, and 
doesn't support any kind of transitive dependencies), LibrarianLib is distributed in its deobfuscated form. This may 
necessitate matching your project's MCP mappings to LibrarianLib's MCP mappings (which can be found in the 
`gradle.properties` file under the `mc_mappings_channel` and `mc_mappings_version` keys).

## Contributing

### Running LibrarianLib
To set up the run configuration for LibrarianLib, run the `genIntellijRuns` or `genEclipseRuns` for the `runtime` 
subproject. 