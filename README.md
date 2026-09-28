# State Graph Search — BFS & DFS Visualization

An interactive, front-end web visualization of Breadth-First Search (BFS) and Depth-First Search (DFS) applied to a graph of US state adjacencies. Watch states light up as the search explores the graph, then watch the path back-track from target to origin in real time — with animated arrows showing the search chain and the final solution direction.

## Features

- **Geographic US map** — States rendered from TopoJSON boundary data via D3's Albers USA projection
- **BFS (Queue) and DFS (Stack)** — Toggle between traversal modes, with identical adjacency data from the Java reference implementation
- **Search Only / Search + Path** — Run a pure search to discover reachability, or trace the full path from start to end
- **Animated back-tracking** — Path states reveal from target back to source, mirroring Java's `buildSolution()` walking `prev` pointers
- **Search arrows** — In Search + Path mode, arrows appear as each state is discovered, showing the backtracking chain (discovered → predecessor). Arrows use label coordinates for consistent positioning and shorten at the target to avoid overlapping state text.
- **Arrow direction flip** — When the solution path is revealed, each exploratory arrow is removed and replaced with a forward-pointing solution arrow (predecessor → state), visually transforming from backtracking chain to the actual path
- **Hover tooltips** — Hover any state to see its name, code, and list of neighbors
- **Speed control** — Adjustable animation speed from 1× to 10×

## Usage

Open [`StateNeighbors-visualization/index.html`](StateNeighbors-visualization/index.html) in any modern browser. No build step, no server required.

1. Pick **Start State** and **End State** from the dropdowns
2. Choose **Stack (DFS)** or **Queue (BFS)** as the traversal mode
3. Choose **Search Only** or **Search + Path**
4. Adjust the **Speed** slider
5. Click **▶ Run Search**

The run button is disabled during search. Click **Reset** to clear the search mid-execution and start fresh.

## Technology

| Component | Version | Source |
|-----------|---------|--------|
| D3.js | v7 | `https://d3js.org/d3.v7.min.js` |
| TopoJSON client | 3 | `https://cdn.jsdelivr.net/npm/topojson-client@3` |
| US boundary data | us-atlas@3 | `https://cdn.jsdelivr.net/npm/us-atlas@3/states-10m.json` |

All data is loaded from CDNs at runtime. The adjacency graph (107 state-pair edges) is embedded directly in the HTML, sourced from the Java reference implementation's `contiguous-usa.txt`.

## Architecture

See [CLAUDE.md](CLAUDE.md) for detailed design decisions, SVG layering strategy, and algorithm notes.

### Layer Architecture

Six SVG `<g>` layers in DOM order (bottom → top): fills, strokes, hover, outlines, arrows, labels. This ensures arrows are always visible above state fills and borders, but below state text labels.

### Arrow System

- **Exploratory arrows** — Grey (`#94a3b8`), drawn during search in Search + Path mode. Points from discovered state → predecessor (backwards along solution chain).
- **Solution arrows** — White (`#ffffff`), flipped during path reveal. Each exploratory arrow is removed and replaced with a forward-pointing arrow (predecessor → state), showing the actual solution path.

## Reference

The visualization mirrors the algorithm in the companion Java project:

- [`StateNeighbors/src/UnitedStates.java`](StateNeighbors/src/UnitedStates.java) — Graph construction and BFS/DFS search
- [`StateNeighbors/src/State.java`](StateNeighbors/src/State.java) — State object with `visited`, `prev`, and neighbor list
- [`StateNeighbors/contiguous-usa.txt`](StateNeighbors/contiguous-usa.txt) — Adjacency data (107 edges, contiguous US)

## Credits

Built using the [Qwen3.6-35B-A3B](https://qwenlm.github.io/) model with Claude Code tooling.

## License

BSD 3-Clause License — see [LICENSE](LICENSE)
