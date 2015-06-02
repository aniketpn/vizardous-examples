package vizardous.examples;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vizardous.model.impl.Cell;
import vizardous.model.impl.Clade;
import vizardous.model.impl.Forest;
import vizardous.model.impl.Phylogeny;

/**
 * This class computes the number of cells that show a fluorescence intensity
 * exceeding a provided threshold.
 * 
 * It demonstrates the creation of standard workflows for analysis data from
 * single-cell experiments using <a
 * href="https://github.com/modsim/vizardous">Vizardous</a>. You can also use
 * this project as a starting point for your own analyses. Just change the
 * contents of the {@link #process()} method to your liking and work with your
 * data files.
 * 
 * @author Stefan Helfrich <s.helfrich@fz-juelich.de>
 */
public class CountCellsWithIncreasedFluorescenceIntensity {

	/** The {@link Forest} to operate on */
	private Forest forest = null;

	/** Number of cells with increased fluorescence intensity */
	private int numberOfCellsWithIncreasedFluorescenceIntensity = 0;

	/** Threshold used for determining "increased fluorescence intensity" */
	private double threshold = 5.0d;

	/**
	 * Constructs a new {@link CountCellsWithIncreasedFluorescenceIntensity}
	 * instance from a {@link Forest}.
	 * 
	 * @param forest
	 * @param thresholds
	 */
	public CountCellsWithIncreasedFluorescenceIntensity(Forest forest) {
		this.forest = forest;
	}

	/**
	 * This main method shows the standard flow for creating analysis scripts.
	 * 
	 * <ol>
	 * <li>Load phyloXML file</li>
	 * <li>Load metaXML file</li>
	 * <li>Create {@link Forest}</li>
	 * <li>Get {@link Phylogeny} from {@link Forest}</li>
	 * <li>Iterate the {@link Phylogeny} and do some computations</li>
	 * </ol>
	 * 
	 * @param args
	 *            Commandline arguments (not used).
	 */
	public static void main(String[] args) {
		/* Load the phyloXML that is provided with this repository. */
		URL phyloXmlUrl = CountCellsWithIncreasedFluorescenceIntensity.class.getResource("/long_term_sos.xml");
		File phyloXML = new File(phyloXmlUrl.getFile());

		/* Load the metaXML that is provided with this repository. */
		URL metaXMLUrl = CountCellsWithIncreasedFluorescenceIntensity.class.getResource("/long_term_sos_meta.xml");
		File metaXML = new File(metaXMLUrl.getFile());

		/* Create the central component storing all the information. */
		Forest forest = new Forest(phyloXML, metaXML);

		/*
		 * Create a new instance and initialize it with the previously created
		 * Forest instance.
		 */
		CountCellsWithIncreasedFluorescenceIntensity counter = new CountCellsWithIncreasedFluorescenceIntensity(forest);
		counter.process();

		/*
		 * Print the number of cells with a fluorescence intensity over the
		 * threshold.
		 */
		System.out.println(String.format("%d cells have a fluorescence intensity over %.1f", counter.getNumberOfCellsWithIncreasedFluorescenceIntensity(), counter.getThreshold()));
	}

	/**
	 * This method is responsible for processing the available data.
	 */
	public void process() {
		/*
		 * Get a list of all available phylogenies. The number of phylogenies is
		 * usually determined by the number cells at the beginning of a
		 * single-cell experiment. However, cells that enter the experiment from
		 * the outside will generate additional phylogenies.
		 */
		List<Phylogeny> phylogenies = forest.getPhyloxml().getPhylogenies();

		for (Phylogeny phylogeny : phylogenies) {
			/*
			 * Clades are objects that only contain structural information from
			 * phyloXML files. Cellular characteristics are stored in Cell
			 * objects.
			 */
			Clade rootClade = phylogeny.getRootClade();
			Cell rootCell = rootClade.getCellObject();

			/* Iterate the phylogeny in a depth-first manner */
			Iterator<Cell> phylogenyIter = rootCell.iterator();

			while (phylogenyIter.hasNext()) {
				Cell cell = phylogenyIter.next();

				/*
				 * Obtain cellular information, e.g. cell length, cell area,
				 * fluorescence intensities, etc...
				 * 
				 * Fluorescence intensities are stored in a Map, where the keys
				 * of the Map are generate from the name of the fluorescence
				 * channel as determined in the metaXML file.
				 */
				Map<String, Double> fluorescences = cell.getFluorescences();
				Double yfpFluorescence = fluorescences.get("yfp");

				/*
				 * Check if the cell shows an increased fluorescence intensity
				 * above the provided threshold.
				 */
				if (yfpFluorescence > this.threshold) {
					numberOfCellsWithIncreasedFluorescenceIntensity++;
				}
			}
		}
	}

	/**
	 * 
	 * @return the number of cells with increased fluorescence intensity
	 */
	public int getNumberOfCellsWithIncreasedFluorescenceIntensity() {
		return numberOfCellsWithIncreasedFluorescenceIntensity;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

}
