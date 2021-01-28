# Mirage Resources
(incomplete, will complete Later™)

#### com.teamwizardry.libarianlib.mirage
- A virtual resource pack, allowing custom resources to be injected at runtime
- Methods to directly inject a single resource location 
- Methods to add dynamic virtual resource pack objects


# How it works

The first step in creating the virtual resources is to add ourselves as a last-resort to every 
`FallbackResourceManager`. This involves injecting code into a few methods, but is pretty straightforward.

The wrinkle in this plan is that the `SimpleReloadableResourceManager` keeps an index of what resource managers
handle what namespaces. Presumably this is an optimization so that it doesn't query every mod's resource manager for 
every single resource. This means that things work perfectly so long as a "real" resource manager exists for the 
namespace in question. However, if we add a resource to a namespace that doesn't have any "real" assets, the 
`SimpleReloadableResourceManager` will fail fast and skip over all the `FallbackResourceManagers` that contain our 
virtual resources. To get around this we keep our own blank `FallbackResourceManager` (our virtual resource pack will 
have been injected into this instance) and hook into the `SimpleReloadableResourceManager`'s failure conditions to fall
back on it.

Language maps are loaded way too early, before almost any mod code has run, so we have to hook into those directly 
instead of dynamically generating lang files.