package citation.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import citation.models.Citation;
import citation.models.Tag;

@RestController
public class CitationController {
		
		
		@RequestMapping(method = RequestMethod.POST)
		public Citation getCitationByText(String text) {
			
			//TODO call API
			List<Citation> citations = new ArrayList<Citation>();
			
			Citation citationFound = null;
			for(Citation citation: citations) {
				if(text.contains(citation.citation)) {
					citationFound = citation;
					break;
				}
			}
			
			if(citationFound != null) {
				
				for(Citation citation: citations) {
					Collection<Tag> similar = new HashSet<Tag>(citation.tags);
					similar.retainAll(citationFound.tags);
					if(similar.size() > 0) {
						citationFound.citationConnexes.add(citation);
					}
				}
			}
			return citationFound;
		}

}
