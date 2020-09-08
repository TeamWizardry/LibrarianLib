# LibrarianLib Foundation
(incomplete, will complete Later™)

A set of base classes and functionality designed to reduce the number of menial and error-prone tasks in Minecraft 
modding.

## Tentative todo
Here's a list of ideas of stuff to add

- POIs:
    - `@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)` (how do I use the Kottle bus for this? The enum 
    hard-codes the java bus)
    - Trees don't generate because of a weird thing: https://discordapp.com/channels/176780432371744769/179315645005955072/704084392229994536

### Items
- Base items for common types
    - Tools
    - Armor
    - Food?
    - Sword/Shield

### Blocks
- Base blocks for common block types
    - Simple block
    - Directional block
    - Full set of building blocks (full block, slabs, stairs)
    - Fluids
- Base blocks for speciality block types
    - Saplings/trees
    - Crops
    - Multi-block crops? (e.g. sugar cane, pumpkins)
    - etc.?

#### Registration & Behavior
- `RenderTypeLookup.setRenderLayer`
- 
- POIs:
    - `Block.isEmissiveRendering`
    - Material opaque vs block properties opaque are separate.

### Block Entities
- Automatic (de)serialization and syncing
- Simple inventory handling (e.g. easily create inventories and assign them to sides for IO)


### Entities
- Automatic (de)serialization and syncing

#### Entity renderers

### Network/Packets
- Automatic packet serialization
- Easily send packets to specific players, everyone around a point, everyone on the server, etc.
- Container GUI messaging (probably part of the facade container system)
    - Annotate methods with `@MessageAction`, then the clientside can just call `sendMessage("methodName", arg, arg)` 
    and it'll serialize and deserialize for you.
    ```kotlin
    class SomeContainer: FacadeContainer {
        @MessageAction
        fun setConfig(index: Int, config: ItemConfig, replace: Boolean) {
            // ...
        }
    }
    class SomeContainerScreen: FacadeContainerScreen {
        init {
            // ...
            var editingIndex: Int
            var creatingNewConfig: Boolean
            configApplyButton.BUS.hook<MouseClickEvent> {
                this.container.sendMessage("setConfig", editingIndex, configOptionsLayer.buildConfig(), !creatingNewConfig)
            }
        }
    }
    ```

### Capabilities
- Capabilities are a gigantic PITA, so make them… not that.
- Simple ICapabilityProvider implementation
- Simple Capability.IStorage implementation
- Automatic (de)serialization (potentially in the IStorage implementation, meaning the capability needs no base class?)
- Entity (& block?) capability synchronization
- Attaching capabilities

### Containers
This probably would largely be contained in a module like Facade, though some registration tasks would be handled by 
foundation.

### Commands
Complex, but doable. Probably a Kotlin builder DSL

### Compat helpers
- WAILA / The One Probe
- JEI

### Misc.

#### Tags
- Look into tags to see if there's anywhere I can streamline

#### Data generators
- Look into data generators to see if there's anywhere I can streamline

