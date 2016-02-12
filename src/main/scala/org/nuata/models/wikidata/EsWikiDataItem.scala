package org.nuata.models.wikidata

/**
 * Created by nico on 08/02/16.
 */
case class EsWikiDataItem(id: String,
                          labels: Array[EsLocal],
                          claims: Array[EsClaim],
                          siteLinks: Array[SiteLink]) {
}