caches.open('fitness-static-v9').then(cache => {
  cache.keys().then(keys => {
    const urls = keys.map(key => {
      const url = key.url
      return url.substring('http://localhost'.length);
    });
    const urlSet = new Set(urls);
    const unique_urls = [];
    urlSet.forEach(value => {
      unique_urls.push(value);
    })
    console.log(unique_urls);
  })
})